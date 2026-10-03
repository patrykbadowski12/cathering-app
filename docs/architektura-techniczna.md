# 🏗️ Architektura Techniczna: Portal SaaS dla Cateringów — Monolit → AWS

> **Status dokumentu:** architektura docelowa (wizja). Konkretne, podjęte decyzje są w [`decisions/`](decisions/README.md). **Przy konflikcie wygrywa ADR.**

Filozofia: **modularny monolit od pierwszego dnia, podział na serwisy tylko wtedy, gdy realny ból to uzasadni** (nie "bo tak się robi"). Granice modułów projektujemy już teraz tak, żeby wydzielenie serwisu w przyszłości było operacją mechaniczną, a nie przepisywaniem logiki.

---

## 0. Zasady projektowe obowiązujące od Fazy 1

Te decyzje podejmujesz **teraz**, bo zmiana ich później jest bolesna:

1. **Moduły komunikują się tylko przez interfejsy publiczne (porty) i zdarzenia domenowe — nigdy przez bezpośrednie odwołania do repozytoriów innego modułu.** To jedyna rzecz, która realnie decyduje, czy podział na mikroserwisy za rok będzie łatwy czy koszmarny.
2. **Każdy moduł ma własne pakiety `domain/infrastructure/web`** i nie importuje klas `infrastructure` innego modułu — tylko jego `domain`/publiczne API.
3. **Migracje bazy danych przez Flyway od pierwszego commita.** Bez tego multi-tenancy w Fazie 2 i podział bazy w przyszłości to piekło.
4. **Zdarzenia domenowe (`DomainEvent`) publikowane przez `ApplicationEventPublisher` od Fazy 1**, nawet w monolicie. To jest Twój przyszły "szew" do wycięcia serwisu — zamieniasz publisher in-memory na SQS/SNS i logika się nie zmienia.
5. **`TenantContext` przez `ThreadLocal` + Spring MVC (blocking), nie WebFlux/korutyny w Fazach 1–3.** WebFlux + ThreadLocal to gwarantowany ból (kontekst gubi się przy przełączeniu wątku). Jeśli kiedyś zechcesz reaktywności, to osobna, świadoma decyzja architektoniczna, nie coś, w co wchodzisz przypadkiem.

---

## 1. Stack technologiczny (rekomendacja)

| Warstwa | Wybór | Uzasadnienie |
|---|---|---|
| Język / framework | Kotlin + Spring Boot 3.x | zgodnie z Twoim planem, dobra integracja z AWS SDK |
| Build | Gradle (Kotlin DSL) | lepsza integracja z Kotlin niż Maven, łatwiejsze multi-module |
| Baza danych | PostgreSQL (RDS) | `jsonb` dla theme_config, dojrzałe wsparcie multi-tenancy |
| Migracje | Flyway | wersjonowanie schematu, wymagane przy CI/CD |
| Renderowanie | Thymeleaf (SSR) | Faza 1–3; prostsze niż SPA, dobre SEO dla stron cateringów |
| Cache / sesje | Redis (ElastiCache) | dopiero od Fazy 3+, gdy pojawi się realna potrzeba |
| Kolejka zdarzeń | `ApplicationEventPublisher` (in-process) → SQS/SNS przy podziale na serwisy | patrz sekcja 4 |
| Kontener | Docker | jeden obraz na start, wieloetapowy build (Gradle build stage + slim JRE runtime) |
| CI/CD | GitHub Actions → ECR → AWS | budowa obrazu, testy, deploy |
| Obserwowalność | Spring Actuator + CloudWatch | metryki, healthcheck, logi |

---

## 2. Struktura modułów (Gradle multi-module od Fazy 1)

Zamiast jednego modułu Gradle z pakietami, **osobne moduły Gradle już w Fazie 1** — to wymusza dyscyplinę (moduł fizycznie nie może zaimportować czegoś, do czego nie ma zależności w `build.gradle.kts`):

```
catering-platform/
├── build.gradle.kts (root)
├── settings.gradle.kts
│
├── shared-kernel/              # DomainEvent, TenantContext, wspólne typy
│   └── build.gradle.kts
│
├── module-diet/                # Faza 1
│   ├── domain/                 # encje, porty (interfejsy), logika biznesowa
│   ├── infrastructure/         # implementacje repozytoriów (JPA), adaptery
│   └── web/                    # kontrolery Thymeleaf/REST
│
├── module-order/                # Faza 1
│   └── (ta sama struktura)
│
├── module-tenant/               # Faza 2
│   └── (ta sama struktura)
│
├── module-billing/               # Faza 4
│   └── (ta sama struktura)
│
├── module-logistics/             # Faza 5+
│   └── (ta sama struktura)
│
└── app/                          # moduł spinający — Application.kt, konfiguracja Spring
    └── build.gradle.kts          # zależy od wszystkich module-*
```

**Reguła zależności:** `module-order` może zależeć od `module-diet:domain` (żeby znać `DietId`), ale nigdy od `module-diet:infrastructure`. `app` jako jedyny zależy od wszystkiego i spina Spring Context.

To jest dokładnie granica, po której za rok "wytniesz" `module-billing` do osobnego repo i osobnego serwisu — zależności są już jawne i wymuszone przez build system, nie przez samodyscyplinę.

---

## 3. Multi-tenancy — szczegóły implementacji (Faza 2)

**Rozpoznawanie tenanta:**
```kotlin
// shared-kernel
object TenantContext {
    private val current = ThreadLocal<TenantId>()
    fun set(id: TenantId) = current.set(id)
    fun get(): TenantId = current.get() ?: throw NoTenantException()
    fun clear() = current.remove()
}
```

`TenantInterceptor` (Spring `HandlerInterceptor`) parsuje `Host`, rozwiązuje `subdomain → tenant_id` (z cache w Redis od Fazy 3, wcześniej zwykłe query z indeksem), ustawia `TenantContext`, czyści go w `afterCompletion` (krytyczne — inaczej wyciek między requestami przy connection poolingu wątków).

**Izolacja danych — Hibernate filter, nie ręczne `WHERE tenant_id = ?` w każdym query:**
```kotlin
@FilterDef(name = "tenantFilter", parameters = [ParamDef(name = "tenantId", type = "uuid")])
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Entity
class Diet(...)
```
Filtr włączasz globalnie w interceptorze na podstawie `TenantContext`. To eliminuje najgroźniejszy błąd (zapomniany filtr w jednym query, wyciek danych między tenantami) na poziomie frameworka, nie code review.

**Dodatkowa warstwa bezpieczeństwa (rekomendowane od Fazy 2, nie opcjonalne):** PostgreSQL Row-Level Security jako drugi, niezależny od Hibernate mur:
```sql
ALTER TABLE diets ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON diets
  USING (tenant_id = current_setting('app.current_tenant')::uuid);
```
Ustawiasz `app.current_tenant` na początku każdej transakcji. Nawet jeśli ktoś (Ty, za pół roku, zmęczony) zapomni filtra Hibernate w nowym query, baza fizycznie nie zwróci cudzych danych.

---

## 4. Droga od monolitu do serwisów — konkretny mechanizm

To jest odpowiedź na Twoje "budujemy pierw monolit, potem dzielimy jeśli trzeba":

**Krok 1 (masz to od Fazy 1):** moduły komunikują się przez `DomainEvent` + `ApplicationEventPublisher`:
```kotlin
// module-order publikuje, nie wie kto słucha
eventPublisher.publishEvent(OrderPlacedEvent(orderId, tenantId, items))

// module-logistics nasłuchuje, nie wie skąd przyszło
@EventListener
fun onOrderPlaced(event: OrderPlacedEvent) { ... }
```

**Krok 2 — kiedy wydzielać serwis (konkretne sygnały, nie "bo mikroserwisy są modne"):**
- moduł ma inny profil skalowania niż reszta (np. `logistics` z ciężkim algorytmem routingu obciąża CPU, podczas gdy `diet`/`order` to głównie I/O)
- moduł musi być wdrażany częściej/niezależnie (np. `billing` zmienia się przy każdej zmianie przepisów podatkowych)
- osobny zespół przejmuje moduł

Bez tych sygnałów zostań w monolicie — to nie jest "gorsza" architektura, tylko właściwa na tym etapie.

**Krok 3 — mechanika wydzielenia (gdy nadejdzie moment):**
1. `module-billing` (moduł Gradle) → osobne repo, osobny Docker image
2. `ApplicationEventPublisher.publishEvent()` → zamiana na `SqsClient.sendMessage()` (jeden adapter w `infrastructure`, `domain` się nie zmienia)
3. `@EventListener` w nowym serwisie → SQS consumer nasłuchujący tej samej kolejki
4. Baza: `module-billing` dostaje własny schemat/bazę dopiero jeśli faktycznie potrzebuje — na start może nadal czytać z tej samej RDS (inny connection pool, osobny użytkownik DB z ograniczonymi uprawnieniami)

Dzięki temu, że granice były jawne od Fazy 1 (osobny moduł Gradle, komunikacja przez zdarzenia), ten krok to głównie przenoszenie plików i podmiana jednego adaptera — nie przepisywanie logiki biznesowej.

---

## 5. AWS — infrastruktura per faza

**Faza 0–1 (walidacja + MVP jednotenantowe): tanio i prosto**
- **Elastic Beanstalk** (Docker platform) lub pojedynczy **EC2 t3.micro/small** — nie komplikuj sobie życia Kubernetes/ECS na tym etapie
- **RDS PostgreSQL** (db.t3.micro, free tier na start)
- **Route 53** dla domeny + certyfikat **ACM** (darmowy)
- Koszt: praktycznie 0–20 USD/mies. w ramach free tier

**Faza 2 (multi-tenancy, subdomeny): dochodzi routing**
- **Route 53** z wildcard rekordem `*.catering.pl → ALB`
- **ACM wildcard certificate** (`*.catering.pl`) — darmowy, auto-renewal
- **Application Load Balancer** przed EC2/Beanstalk (jeśli jeszcze go nie masz)
- RDS bez zmian — jedna baza, `tenant_id` w tabelach

**Faza 3 (panel admina, upload plików):**
- **S3** na logo/assety tenantów, bucket per-nic-nie-trzeba — jeden bucket, prefiks `tenants/{tenant_id}/logo.png`
- **CloudFront** przed S3 dla szybkiego ładowania logo/obrazków globalnie
- Rozważ przejście z EC2/Beanstalk na **ECS Fargate** (jeden task definition, łatwiejsze skalowanie, bez zarządzania serwerami) — to dobry moment, bo aplikacja jest już ustabilizowana

**Faza 4 (płatności, zdarzenia asynchroniczne):**
- **SQS** — nawet w monolicie warto już teraz przenieść ciężkie/wolne handlery zdarzeń (np. generowanie list produkcyjnych) z synchronicznego `@EventListener` na kolejkę, żeby request użytkownika nie czekał
- **Secrets Manager** na klucze API Stripe/PayU per tenant (nie w zwykłych env varach)

**Faza 5+ (jeśli faktycznie dzielisz na serwisy):**
- **ECS Fargate** z osobnymi task definitions per serwis, albo pierwsze starcie z **EKS** jeśli zespół urośnie (dla jednej osoby — przesada)
- **SNS + SQS** (fan-out) zamiast pojedynczej kolejki, gdy więcej niż jeden serwis nasłuchuje tego samego zdarzenia
- **CloudWatch + X-Ray** dla tracingu między serwisami — bez tego debugowanie rozproszonego systemu solo jest bardzo bolesne

---

## 6. CI/CD (od Fazy 1)

```
GitHub push → GitHub Actions:
  1. gradle test (wszystkie moduły)
  2. gradle build → Docker image
  3. push do Amazon ECR
  4. deploy do Elastic Beanstalk / ECS (aktualizacja task definition)
```
Zainwestuj w to od razu w Fazie 1, nawet dla jednego tenanta — ręczny deploy to strata czasu i źródło błędów, a raz skonfigurowany pipeline działa przez wszystkie kolejne fazy bez zmian.

---

## 7. Obserwowalność — minimalny sensowny zestaw

Od Fazy 2 (gdy masz więcej niż jednego tenanta, błędy przestają być oczywiste):
- **Spring Actuator** (`/actuator/health`, `/actuator/metrics`) → **CloudWatch** (natywna integracja przez CloudWatch Agent)
- Strukturalne logi (JSON, nie plain text) z **zawsze obecnym `tenant_id`** w każdym logu — inaczej debugowanie "co się stało u tenanta X" jest koszmarem
- Alarm CloudWatch na error rate i latency — nawet prosty, żeby nie dowiadywać się o awarii od klienta

---

## 8. Podsumowanie decyzji "monolit → serwisy"

| Pytanie | Odpowiedź na teraz |
|---|---|
| Ile modułów Gradle? | Osobny moduł per bounded context, od Fazy 1 |
| Jak moduły się komunikują? | Tylko przez `domain`-owe interfejsy i `DomainEvent` |
| Kiedy wydzielać serwis? | Gdy sygnał skalowania/zespołu/częstotliwości wdrożeń to uzasadni — nie wcześniej |
| Co ułatwia wydzielenie? | Jawne granice modułów + event-driven komunikacja od początku |
| Gdzie hostować na start? | Elastic Beanstalk/EC2 + RDS, tanio i prosto |
| Kiedy ECS/Fargate? | Faza 3+, gdy aplikacja stabilna i chcesz łatwiejsze skalowanie |
| Kiedy prawdziwa kolejka (SQS)? | Faza 4, dla wolnych/asynchronicznych operacji — niezależnie od podziału na serwisy |

**Najważniejsza zasada na koniec:** granice modułów projektujesz precyzyjnie już teraz (to tanie — to tylko dyscyplina w kodzie), a decyzję o fizycznym podziale na serwisy odkładasz do momentu, aż będziesz mieć konkretny, mierzalny powód. To jest różnica między "monolit-first z głową" a "monolit, który nigdy nie da się podzielić".
