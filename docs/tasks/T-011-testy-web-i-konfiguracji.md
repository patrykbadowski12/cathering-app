# T-011 — Testy warstwy web (`@WebMvcTest`) i konfiguracji tenanta

**Status:** REVIEW · **Etap:** 0 · **Kto:** Ty · **Zależy od:** T-004, T-014 · **Wykonanie:** Claude (na prośbę)

## Cel
Szybkie testy kontrolerów (slice, bez bazy) pilnują kodów HTTP, nagłówków i kształtu JSON-a. Konfiguracja tenanta ma testy jednostkowe.

To uzupełnienie `DietPlanApiErrorsTest` z T-014 (pełny kontekst + Testcontainers), który sprawdza błędy 400/404 end-to-end. Tutaj chodzi o szybką warstwę piramidy: dziesiątki testów w sekundach.

## Czego się uczysz
- **Test slice `@WebMvcTest` vs `@SpringBootTest`:** co ładuje każdy z nich, ile trwa i kiedy którego użyć (piramida testów).
- **`MockMvc` vs `MockMvcTester`:** `MockMvcTester` to nowsze API z asercjami AssertJ. Spróbuj obu i wybierz jedno.
- **Fake vs mock na poziomie web.** Use case'y to klasy z repozytorium w konstruktorze. Możesz:
  - podać prawdziwe use case'y z `InMemoryDietPlanRepository` (już jest w testach `application`) przez `@TestConfiguration`;
  - albo je zamockować (`@MockitoBean` / MockK).

  Ta sama dyskusja co w T-003, tylko poziom wyżej. Uwaga: `TenantId` to `value class`, a mockowanie metod z value class w Mockito bywa kłopotliwe (zmienione nazwy metod w bajtkodzie). Sprawdź, co to oznacza dla Twojego wyboru.

## Kryteria akceptacji
- [x] Testy w `module-diet/src/test/kotlin/com/cateringplatform/diet/web/`, slice `@WebMvcTest` dla `DietPlanAdminController` i `DietPlanShopController`.
- [x] Pokryte scenariusze:
  - `POST /api/v1/admin/diet-plans`: `201`, nagłówek `Location` kończy się `/api/v1/admin/diet-plans/{id}` z `id` z body; body ma `id`, `name`, `kcal`, `state`.
  - `POST` z niepoprawnymi danymi: `400` (zobacz wskazówkę o handlerach).
  - `GET /api/v1/shop/diet-plans`: `200` i tylko plany bieżącego tenanta.
  - `GET` po id (sklep i panel): `200` dla własnego planu; `404` jako `ProblemDetail` dla nieistniejącego i dla planu **innego tenanta**.
  - `GET` z niepoprawnym UUID (`/abc`): `400`.
  - JSON odpowiedzi **nie** zawiera `tenantId`.
- [x] Tenant w testach jest ustawiony świadomie: przez `TenantContext` w teście albo przez filtr. Wybierz i uzasadnij przy review. Podpowiedź: `module-diet` nie widzi `TenantFilter` z `app`.
- [x] `TenantProperties`: testy jednostkowe walidacji sluga (poprawny, z wielką literą, z polskim znakiem, pusty).
- [x] `FixedTenantResolver`: zwraca tenanta z properties, niezależnie od requestu.
- [x] Testy slice nie używają Testcontainers, a całe `./gradlew :module-diet:test` nie zwalnia zauważalnie.

## Wskazówki
- W Boot 4 `@WebMvcTest` jest w osobnym artefakcie (`spring-boot-starter-webmvc-test`). `app` ma go od T-014, `module-diet` jeszcze nie.
- `@WebMvcTest` szuka klasy `@SpringBootConfiguration`. W `module-diet` jest nią `DietTestApplication`, a ona ma `@EnableJpaAuditing`. Sprawdź, co się stanie w slice bez JPA, i zastanów się, jak to rozdzielić.
- `@WebMvcTest` nie wczytuje `DietModuleConfig` (to `@Configuration`, a nie kontroler), więc beany use case'ów musisz dostarczyć sam.
- Slice ładuje `@ControllerAdvice` z modułu (`DietPlanExceptionHandler`, czyli 404), ale **nie** `ApiExceptionHandler` z `app`. Jaką odpowiedź dostaniesz wtedy dla `400` z walidacji? Czy w tym teście sprawdzasz tylko status, czy też kształt błędu? Kształt i tak pokrywa `DietPlanApiErrorsTest`.
