# T-004 — `TenantContext` z domyślnym tenantem z YAML

**Status:** DONE · **Etap:** 0 · **Zależy od:** T-002 · **ADR:** 0007, 0008

## Cel
Każdy request HTTP ma ustawionego tenanta (w Fazie 1 zawsze domyślnego, z `application.yaml`), a `tenantId` przepływa jawnie: kontroler → use case → domena. Mechanizm jest gotowy na podmianę w Fazie 2, gdzie zmieni się tylko implementacja `TenantResolver`.

## Czego się uczysz
- Servlet `Filter` vs `HandlerInterceptor`: kiedy który się wywołuje i co „widzi”. Dlaczego ADR wybiera `Filter`?
- `ThreadLocal` i pula wątków: dlaczego brak `clear()` to wyciek danych między requestami. MDC też jest oparte na `ThreadLocal`, więc dotyczy go to samo.
- `@ConfigurationProperties`: typowana konfiguracja zamiast `@Value("...")`.
- Strategia (Strategy pattern): interfejs `TenantResolver` i wymienne implementacje.
- Kotlin `value class`: typ bez narzutu w runtime.

## Kryteria akceptacji
- [ ] `shared-kernel`: `TenantId` (value class na `UUID`) i `TenantContext` (`set` / `get` / `clear`). `get()` bez ustawionego tenanta rzuca czytelny wyjątek.
- [ ] Interfejs `TenantResolver` zwracający parę *id + slug* (np. `ResolvedTenant(id: TenantId, slug: String)`) i implementacja `FixedTenantResolver`.
- [ ] Domyślny tenant w `application.yaml` (`catering.tenant.default.id` jako **stały UUIDv7** wygenerowany raz, np. na uuidgenerator.net/version7, oraz `catering.tenant.default.slug: brokul`), czytany przez `@ConfigurationProperties`. Brak wartości albo niepoprawny slug (`[a-z0-9-]`) oznacza, że aplikacja **nie startuje**.
- [ ] Servlet `Filter` ustawia `TenantContext` (tylko `TenantId`) i `MDC` (`tenant` = slug), a w `finally` czyści **oba**, także przy wyjątku.
- [ ] Wzorzec logowania pokazuje tenanta, np. `[tenant=brokul]` (`logging.pattern.level` albo `logging.pattern.console` z `%X{tenant}`). Sprawdź w logu przy POST `/diet-plans`.
- [ ] Kontroler przekazuje `TenantId` jawnie do use case'ów. Use case'y i `DietPlan.create(...)` przyjmują `TenantId`, a nie `UUID`.
- [ ] Domena nie importuje `TenantContext` ani nie generuje `tenantId`.
- [ ] Tymczasowa stała z T-002 usunięta.
- [ ] Testy:
  - jednostkowy dla `TenantContext`,
  - test filtra: kontekst i MDC są puste po requeście, także gdy handler rzucił wyjątek.

## Wskazówki
- Gdzie umieścić filtr i resolver? Nie są częścią żadnego bounded contextu. Zastanów się, czy to `app`, czy `shared-kernel`. Zwróć uwagę, że `shared-kernel` musiałby wtedy zależeć od Servlet API, a to temat bliski T-009.
- JPA nie zna `TenantId`. Na granicy encja ↔ domena mapujesz `TenantId ↔ UUID` w adapterze. Alternatywą są konwertery JPA, ale to później.
- Jak kontroler dostaje tenanta: `TenantContext.get()` wprost, czy własny argument metody (`HandlerMethodArgumentResolver`), np. `fun create(tenantId: TenantId, ...)`? Druga opcja jest ładniejsza i łatwiej ją testować. To dobre ćwiczenie, ale opcjonalne.

## Review

### Postęp — 2026-10-10 · gałąź `task/T-004-tenant-context` (kroki 1–2 zamknięte)
- ✅ `TenantId` (value class, pole `value`) w `shared-kernel`. Domena, port, use case'y i fake używają `TenantId`, a `UUID` zostaje tylko w adapterze JPA (encja i `DietPlanRepositoryImpl`).
- ✅ `GetDietPlanUseCase` przyjmuje `DietPlanId`, a konwersja odbywa się w kontrolerze.
- ✅ `TenantContext` jako `object` z jednym prywatnym `ThreadLocal`, `get()` rzuca `NoTenantException` (`IllegalStateException`).
  - Runda pośrednia: wersja z `class` i nowym `ThreadLocal` w `setTenant` gubiła tenanta przy dwóch równoległych wątkach (test wyścigu: `expected <TenantId> but was <null>`).
- ✅ `TenantContextTest`: set/get, brak tenanta, set → clear → wyjątek, izolacja między wątkami, `@AfterEach clear`. Mutacja `clear() = Unit` jest łapana przez 2 testy.
- ✅ `./gradlew build`: 17 testów zielonych (shared-kernel 4, module-diet 12, app 1).
- ⏳ Kroki 3–6: properties, resolver, filtr, kontroler bez `TEMP_TENANT_ID`, MDC i testy filtra.

### Review kroków 3–6 — 2026-10-10 → **CHANGES**

**Jak sprawdzone:**
- `./gradlew build` ✅ (19 testów).
- `bootRun`: POST `201`, GET `200`. W logu `INFO [tenant=testTenant] … DietPlanController : Created dietPlan …`, a logi ze startu mają `[tenant=none]` ✅.
- Mutacja w kopii projektu: `TenantContext.clear()` przeniesione z `finally` do `try` → **testy zielone (mutacja przeżyła)**.

**Co jest dobre:**
- Podział na `TenantProperties` / `TenantResolver` / `FixedTenantResolver` / `ResolvedTenant` / `TenantFilter`.
- `@ConfigurationPropertiesScan`.
- `spring-boot-starter-web` dodany do `app`. Słusznie, bo `implementation` w modułach nie przecieka do `app`.
- `@Order` przed resztą filtrów.
- Testy filtra bez Springa, z lambdą `FilterChain`.
- `TEMP_TENANT_ID` usunięty z kontrolera.
- UUIDv7 w YAML.

**Do poprawy (blokujące):**
1. **Test ścieżki wyjątku nie sprawdza `TenantContext`** (dowód: mutacja wyżej). Drugi test sprawdza tylko MDC. Dodatkowo łańcuch rzuca `NoTenantException`, czyli ten sam typ, który rzuca `TenantContext.get()`, więc nie da się odróżnić, *skąd* wyjątek przyszedł. Rzuć z lambdy inny wyjątek (np. `IllegalStateException("boom")`), a po `assertThrows` sprawdź **oba**: `TenantContext.get()` rzuca `NoTenantException` i `MDC.get(...)` jest `null`.
2. **Walidacja sluga jest niezgodna z ADR 0008.** Regex `[a-zA-Z0-9]+` przepuszcza wielkie litery i odrzuca myślnik, a ADR mówi `[a-z0-9-]+`. Przez to YAML ma `slug: testTenant`, a test `"TEST"`. Żaden z nich nie nadaje się na subdomenę w Fazie 2 (`testTenant.catering.pl` to w DNS i tak `testtenant`). Popraw regex i wartości (np. `brokul`, `test-tenant`).
3. **`require` bez komunikatu.** Przy złym slugu start skończy się błędem `Failed requirement.`, z którego nic nie wynika. Dodaj komunikat z wartością i oczekiwanym formatem.

**Do poprawy (drobne):**
- `ResolvedTenant(id: UUID)` → `id: TenantId`. Resolver zamienia `UUID → TenantId` raz, a filtr tylko przekazuje dalej (tak było w planie zadania).
- `FixedTenantResolver` buduje `ResolvedTenant` przy każdym requeście. Zbuduj go raz (`private val`).
- `TenantProperties` / `DefaultTenantProperties`: `var` → `val`. Konfiguracja po starcie nie powinna się zmieniać, a constructor binding działa z `val`.
- Klucz MDC `"tenant"` jest powtórzony w filtrze i teście (i we wzorcu logów). Przenieś go do stałej, np. w `TenantFilter`.
- `private var tenantFilter` w teście → `val`.
- Komentarz przy `@Order`: lepiej napisać *dlaczego* (`must run before Spring Security (order -100), which will need the tenant in E4`).
- Kontroler: importy z gwiazdką (`web.bind.annotation.*`, `java.util.*`) i nieużywany `kotlin.collections.forEach` (śmieć z auto-importu IDE). W logu `it.toString()` jest zbędne, bo placeholder `{}` sam wywoła `toString()`, i to tylko wtedy, gdy log jest faktycznie wypisywany.

### Runda końcowa — 2026-10-10 → **DONE** ✅

**Jak sprawdzone:**
- `./gradlew build`: 19 testów zielonych (shared-kernel 4, module-diet 12, app 3).
- Mutacja `TenantContext.clear()` przeniesione z `finally` do `try` → **złapana** przez test ścieżki wyjątku.
- Start ze slugiem `Brokuł` → aplikacja nie wstaje: `Invalid tenant slug 'Brokuł': expected [a-z0-9-]+` ✅
- Start bez sekcji `catering.tenant` → aplikacja nie wstaje: `Failed to bind properties under 'catering.tenant'` ✅
- Wcześniej (`bootRun`): log requestu z `[tenant=<slug>]`, logi startowe z `[tenant=none]` ✅

| Kryterium | Wynik |
|---|---|
| `TenantId` + `TenantContext` w `shared-kernel`, `get()` rzuca czytelny wyjątek | ✅ |
| `TenantResolver` → `ResolvedTenant(id: TenantId, slug)` + `FixedTenantResolver` | ✅ |
| Stały UUIDv7 + slug w YAML, brak lub zły slug → brak startu | ✅ |
| Filtr ustawia i czyści `TenantContext` oraz MDC, także przy wyjątku | ✅ (test + mutacja) |
| Wzorzec logów z tenantem | ✅ |
| Kontroler przekazuje `TenantId` jawnie, a domena nie zna `TenantContext` | ✅ |
| `TEMP_TENANT_ID` usunięty | ✅ |
| Testy kontekstu i filtra | ✅ |

**Czego się nauczyłeś w tym zadaniu:**
- `ThreadLocal`: jedna instancja, osobna wartość na wątek (błąd z podmianą `ThreadLocal` przy równoległych requestach).
- `object` jako singleton.
- Typy platformowe z Javy i dlaczego jawny typ zwracany je zatrzymuje.
- `OncePerRequestFilter` i `try/finally`.
- `@ConfigurationProperties` z walidacją, która zatrzymuje start (*fail fast*).
- MDC.
- Kolejność filtrów.
- Refaktor typów prowadzony przez kompilator: domena i port mówią `TenantId`, a `UUID` zostaje tylko na granicy z bazą.
- `IllegalStateException("x")` bez `throw` to tylko obiekt.
