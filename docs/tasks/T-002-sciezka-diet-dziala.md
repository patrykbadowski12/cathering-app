# T-002 — Projekt się kompiluje, POST/GET `/diet-plans` działa

**Status:** DONE · **Etap:** 0 · **Zależy od:** T-001 · **ADR:** 0009

## Cel
`./gradlew build` przechodzi. Plan utworzony przez `POST /diet-plans` jest widoczny w `GET /diet-plans`.

## Czego się uczysz
- **Porty i adaptery** (architektura heksagonalna): port `DietPlanRepository` w domenie, adapter `DietPlanRepositoryImpl` w infrastrukturze i to, kto kogo „zna”.
- Ręczna rejestracja beanów (`@Bean` w `@Configuration`), gdy klasy domenowe nie mają adnotacji Springa.
- Transakcje w Springu: jak działa `@Transactional` (proxy) i dlaczego wywołanie metody z tej samej klasy go omija.

## Kryteria akceptacji
- [ ] Kompilacja bez błędów: `findAllByTenantId` zaimplementowane, importy i nazwy w kontrolerze poprawione, `ListDietPlanUseCase` zarejestrowany jako bean.
- [ ] `tenantId` jest spójny między zapisem a odczytem: przychodzi z zewnątrz (z kontrolera), a ani domena, ani adapter nie generują własnego (ADR 0007). Na razie wystarczy stała w kontrolerze, a w T-004 zastąpi ją `TenantContext`.
- [ ] `GET /diet-plans` zwraca listę z `id`, `name`, `kcal`, `state`.
- [ ] Zapis jest transakcyjny. Wybierz miejsce dla `@Transactional` tak, żeby domena dalej nie znała Springa (ADR 0005), a wybór uzasadnij w rozmowie przy review.
- [ ] `main` przekazuje argumenty: `runApplication<...>(*args)`.
- [ ] Sprawdzone ręcznie (HTTP Client w IntelliJ / curl / Postman). Zapisz przykładowe requesty, np. w `http/diet-plans.http`.

## Wskazówki
- Mapowanie `DietPlanEntity → DietPlan` pojawia się w dwóch miejscach. Gdzie umieścić je raz?
- `findAllByTenantId`: Spring Data potrafi wygenerować zapytanie z samej nazwy metody w `DietPlanJpaRepository` (*derived query methods*).
- Zgodnie z ADR 0007 `DietPlan.create()` i use case przyjmują `tenantId` jako parametr. Możesz od razu zrobić to na `UUID`, a w T-004 zamienisz go na `TenantId`.

## Review

### Runda 1 — 2026-10-04 · gałąź `T-002` (niezacommitowane zmiany) → **CHANGES**

**Jak sprawdzone:**
- `./gradlew build` przechodzi.
- `bootRun` z `show-sql` i logami `JpaTransactionManager`.
- POST, GET i POST z pustą nazwą przez curl.

| Kryterium | Wynik |
|---|---|
| Kompilacja, `findAllByTenantId`, bean `ListDietPlanUseCase` | ✅ |
| `tenantId` spójny, z zewnątrz, adapter nie losuje | ✅ POST → GET zwraca ten sam plan |
| GET zwraca `id`, `name`, `kcal`, `state` | ✅ |
| Transakcyjność bez Springa w domenie | ✅ warstwa `application` + `@Transactional` (→ ADR 0009). W logach: `Creating new transaction … CreateDietPlanUseCase`, a przy błędzie walidacji `Rolling back` |
| `runApplication(*args)` | ✅ |
| Requesty w `.http` | ✅ `docs/http/dietPlans.http` |

**Co jest bardzo dobre:**
- Samodzielnie wydzielona warstwa `application` i `readOnly = true` na odczycie.
- Mapowanie encja → domena w jednym miejscu (`toDietPlan()`), DTO odpowiedzi z `fromDomain()` w osobnym pliku.
- Nazwa `ListDietPlanUseCase` lepiej oddaje intencję niż `Get…`.

**Do poprawy (blokujące):**
1. **SELECT przed każdym INSERT.** W logu przy POST widać najpierw `select … from diet_plans where id=?`, a dopiero potem `insert`. Przyczyna: ID nadajesz sam (w domenie), więc Spring Data `save()` widzi niepuste `id`, uznaje encję za *istniejącą* i robi `merge` zamiast `persist`. `merge` musi najpierw sprawdzić, czy wiersz istnieje.
   - Skutki: podwójne zapytanie przy każdym zapisie, a gdyby ID się powtórzyło, `merge` **nadpisałby** cudzy wiersz zamiast rzucić błąd.
   - Do doczytania: `Persistable<ID>` i `isNew()` w Spring Data JPA, oraz jak `@Version` wpływa na tę decyzję.
   - Kryterium: w logu przy POST jest **tylko** `insert`.
2. **Ręczna wersja `jackson-module-kotlin:3.1.5`** w `module-diet/build.gradle.kts`. Wersjami zarządza BOM Spring Boota, a ręcznie wpisana wersja przestanie się zgadzać przy następnym podbiciu Bootiego (teraz akurat obie to 3.1.5). Usuń numer wersji. Zastanów się też, czy ta zależność nie powinna być raz, w `app`: ObjectMapper żyje w kontekście aplikacji, a każdy kolejny moduł z kontrolerami potrzebowałby jej znowu.
3. **POST zwraca `200 OK`.** Utworzenie zasobu to `201 Created`, najlepiej z nagłówkiem `Location: /api/v1/diet-plans/{id}`. Do doczytania: `ResponseEntity.created(uri)`.

**Drobne (nie blokują):**
- `DietModuleConfig`: literówka `listDietPlanUseCase` (*User* zamiast *Use*). W kontrolerze pole nazywa się `listDietPlanUseCase`, a klasa `ListDietPlanUseCase`. Ujednolić liczbę mnogą.
- `ListDietPlansUseCase.execute(tenant: UUID)`: parametr nazwij `tenantId`, tak jak w `CreateDietPlanUseCase`.
- `@RequestMapping("api/v1/diet-plans")`: działa, ale konwencją jest wiodący `/`.
- Stała tenanta w kontrolerze: nazwij ją tak, żeby krzyczała, że jest tymczasowa (np. `TEMP_TENANT_ID` + `// TODO(T-004)`).
- `findById`: `?.let { it.toDietPlan() }` to po prostu `?.toDietPlan()`. Spring Data ma też Kotlinowe `findByIdOrNull()`.
- `override fun findAllByTenantId(...) =` bez typu zwracanego. Dla publicznych funkcji warto pisać typ jawnie, bo czyta się to jak kontrakt.
- `save()` buduje encję „ręcznie”. Symetrycznie do `toDietPlan()` można dodać `DietPlanEntity.fromDomain(dietPlan)`.
- Pusta nazwa daje `500` i stack trace w logu. Poprawnie byłoby `400`, ale to celowo zakres E1 (`@ControllerAdvice`).
- Gałąź nazywa się `T-002`, a konwencja z `docs/README.md` to `task/T-002-…`. Nie zmieniaj teraz, tylko trzymaj się konwencji od następnej.

**Pytanie kontrolne (odpowiedz w rozmowie):** gdyby `CreateDietPlanUseCase` miał drugą metodę i wołał ją przez `this.inna()`, czy ta druga miałaby własne ustawienia `@Transactional`? Dlaczego?

> Odpowiedź (z rozmowy): „nie powstanie nowa transakcja”. To połowicznie trafne. Pełna odpowiedź: `this.inna()` omija proxy, więc adnotacja na `inna()` jest **ignorowana w całości** (`REQUIRES_NEW`, `readOnly`, `rollbackFor`, `timeout`). Przy `REQUIRED` tego nie widać, bo działa transakcja otwarta przez `execute()`. Jeśli jednak `execute()` nie ma `@Transactional`, to `inna()` wykona się **bez transakcji**.

### Runda 2 — 2026-10-04 → **CHANGES**

**Jak sprawdzone:** `./gradlew build` ✅, `bootRun` + curl na wszystkie trzy endpointy.

**Co naprawione:**
- ✅ SELECT przed INSERT zniknął. W logu przy POST jest tylko `insert`.
- ✅ Jackson przeniesiony do `app`, bez numeru wersji.
- ✅ Wiodący `/`, nazwa `TEMP_TENANT_ID` + TODO, `?.toDietPlan()`, jawne typy w repozytorium, `fromDomain()` w encji.
- ✅ `findByIdAndTenantId` zamiast `findById`: bezpieczeństwo między tenantami przemyślane na poziomie portu.

**Błędy (wynik uruchomienia):**

| Request | Oczekiwane | Faktycznie |
|---|---|---|
| `POST /api/v1/diet-plans` | `201`, nagłówek `Location: …/diet-plans/{id}`, body z planem | **`200`, body `{}`, brak `Location`** |
| `GET /api/v1/diet-plans/{id}` | `200` z planem | **`404`** (endpoint nie istnieje) |
| `GET /api/v1/diet-plans/id` | — | **`500`** `MissingPathVariableException` |

1. **POST: `ResponseEntity.created(uri)` zwraca *builder*, a nie `ResponseEntity`.** Brakuje `.body(…)`, więc Spring serializuje do JSON-a sam obiekt buildera (stąd `{}` i `200`), a `DietPlanResponse` z poprzedniego `let` przepada. Kompilator tego nie złapał, bo funkcja ma ciało-wyrażenie bez typu zwracanego. Z jawnym `: ResponseEntity<DietPlanResponse>` dostałbyś błąd kompilacji. To dokładnie argument z rundy 1 za jawnymi typami.
2. **URI w `Location`:** wstrzyknięty `UriComponentsBuilder` zawiera tylko bazę (`http://localhost:8080`), bez ścieżki. Samo `.build()` dałoby więc adres serwera, a nie zasobu. Trzeba dokleić ścieżkę z id (`path(…)` + `buildAndExpand(…)`).
3. **`@GetMapping("/id")`** to dosłowny tekst „id”, a nie zmienna. Szablon zmiennej ścieżki zapisuje się w nawiasach klamrowych.
4. **GET zwraca `ResponseEntity<DietPlan>`**, czyli obiekt domenowy prosto w API. Wypływa wtedy `tenantId`, a każda zmiana w domenie zmienia kontrakt API. Użyj `DietPlanResponse` tak samo jak w pozostałych endpointach.

**Do decyzji: kto nadaje ID?**

SELECT zniknął, bo ID nadaje teraz Hibernate (`@GeneratedValue`), a nie domena. Ma to jednak skutki uboczne:
- `DietPlan.id` jest teraz **nullable**. Agregat istnieje bez tożsamości, a w kodzie pojawiły się `!!` (`DietPlanResponse`, `toDietPlan`). Każde `!!` to potencjalny `NullPointerException`.
- To kłóci się z kierunkiem z ADR 0008 i T-006 (UUIDv7 generowany przez nas). Hibernate generuje domyślnie v4.
- Domena bez ID nie może w przyszłości np. opublikować zdarzenia `DietPlanCreated(id)` przed zapisem.

Rekomendacja: ID nadaje domena (`DietPlanId` non-null, generowane w `create()`), a problem SELECT-a rozwiązuje encja przez `Persistable<UUID>` z `isNew()`. To była sugestia z rundy 1. Jeśli wolisz zostać przy generowaniu przez Hibernate, porozmawiajmy. To zmiana ADR 0008, a nie tylko szczegół implementacji.

**Drobne:**
- Nieużywane importy w `DietPlanJpaRepository` (`org.hibernate.annotations.TenantId`, `DietPlanId`). Ciekawostka: `@TenantId` to wbudowany mechanizm multi-tenancy Hibernate'a, warto go rozważyć w Fazie 2 obok filtrów i RLS.
- Drugi konstruktor w `DietPlanEntity` jest zbędny, bo główny ma już `id = null` jako domyślne, a nazwane argumenty wystarczą. Jeśli przejdziesz na ID z domeny, zniknie sam.
- `GetDietPlanUseCase.execute(id, tenantId)`: w `Create` i `List` `tenantId` jest pierwszym parametrem. Warto trzymać jedną kolejność. Use case może też przyjmować `DietPlanId`, a nie gołe `UUID`.
- `docs/http/dietPlans.http`: dodaj GET po id. IntelliJ HTTP Client potrafi zapisać `Location` z odpowiedzi POST do zmiennej i użyć jej w następnym requeście (response handler, `client.global.set(...)`).

### Runda 3 (częściowa, czytanie kodu) — 2026-10-04 → **CHANGES**
- ✅ ID nadaje domena (`DietPlanId` non-null), `!!` zniknęły, drugi konstruktor encji usunięty. Decyzja „ID z domeny” jest zgodna z ADR 0008.
- ❌ **Brak `Persistable<UUID>` w `DietPlanEntity`**, więc SELECT przed INSERT **wrócił**. To ten sam układ co w rundzie 1: przypisane ID i `isNew()` zwraca `false`.
- ❌ POST: `uriComponentsBuilder.build(it.id)` nadal daje `http://localhost:8080`, bo builder nie ma ścieżki, a przekazany argument nie ma czego podstawić. Brak też `.body(...)`.
- ❌ GET `/{id}`: mapping poprawiony ✅, ale nadal zwraca `DietPlan` zamiast `DietPlanResponse`.
- ⏳ Nieużywane importy w `DietPlanJpaRepository`, GET po id w `.http`.

### Runda 4 — 2026-10-04 → **DONE** ✅

**Jak sprawdzone:** `./gradlew build` ✅, `bootRun` z `show-sql` i logami `JpaTransactionManager`.

| Scenariusz | Wynik |
|---|---|
| POST | `201 Created`, `Location: …/api/v1/diet-plans/{id}`, body z planem ✅ |
| GET pod adres z `Location` | `200` z tym samym planem ✅ |
| GET lista | `200` ✅ |
| GET nieistniejące id | `404` ✅ |
| GET niepoprawny UUID (`/abc`) | `400` ✅ (Spring sam waliduje konwersję `@PathVariable`) |
| POST z pustą nazwą | `500` + `Rolling back` w logu: transakcja działa, a mapowanie na `400` to zakres E1 |
| SQL przy POST | **tylko `insert`**, bez SELECT-a przed nim (`Persistable` działa) ✅ |
| Transakcje | każdy use case otwiera własną transakcję, odczyty `readOnly` ✅ |

**Posprzątaj przed commitem (nie blokuje):**
- `DietPlanEntity`: komentarz `// (1) private!` skopiowany z przykładu. Warto go zastąpić krótkim *dlaczego*, np. „private, żeby nie kolidował z `Persistable.getId()`”.
- `DietPlanJpaRepository`: nieużywane importy `TenantId` i `DietPlanId`.
- `dietPlans.http`: GET po id ma na sztywno wpisany UUID, który przy H2 in-memory jest nieaktualny po każdym restarcie. Podpowiedź: response handler w POST (`client.global.set("dietPlanId", response.body.id)`) i `{{dietPlanId}}` w GET.
- `GetDietPlanUseCase.execute(id, tenantId)`: kolejność parametrów inna niż w pozostałych use case'ach.

**Czego się nauczyłeś w tym zadaniu (podsumowanie):** porty i adaptery, warstwa `application` jako granica transakcji (ADR 0009), proxy i self-invocation, `isNew()`/`Persistable` przy ID nadawanym przez domenę, `201 + Location`, ochrona przed odczytem zasobu innego tenanta (`findByIdAndTenantId` → 404) i wartość jawnych typów zwracanych.
