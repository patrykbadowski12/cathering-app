# T-004 — `TenantContext` z domyślnym tenantem z YAML

**Status:** IN PROGRESS · **Etap:** 0 · **Zależy od:** T-002 · **ADR:** 0007, 0008

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
