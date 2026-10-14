# T-011 — Testy warstwy web i konfiguracji tenanta

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-004

## Cel
To, co dziś sprawdzamy ręcznie curlem (kody HTTP, `Location`, kształt JSON-a, 404 dla cudzego tenanta), jest pilnowane przez szybkie testy automatyczne. Walidacja konfiguracji tenanta też ma własne testy.

## Czego się uczysz
- **Test slice** `@WebMvcTest`: kontekst Springa tylko z warstwą web (kontroler, Jackson, walidacja, filtry), bez bazy i JPA. Szybszy niż `@SpringBootTest`.
- `MockMvc` (albo `MockMvcTester` / `WebTestClient` w MockMvc mode, porównaj): wysyłanie requestów i asercje na statusie, nagłówkach i JSON-ie (`jsonPath`).
- Podstawianie zależności w slice: use case'y to klasy (nie interfejsy) z repozytorium w konstruktorze. Zastanów się, czy podać je jako prawdziwe obiekty z fake'owym repozytorium (`@TestConfiguration` + `@Bean`), czy zamockować (`@MockitoBean` / MockK). To ta sama dyskusja *fake vs mock* co w T-003, tylko na innym poziomie.
- Testy kontraktu API: test pilnuje, że zmiana w domenie nie zmienia po cichu JSON-a.

## Kryteria akceptacji
- [ ] Testy kontrolera w `module-diet/src/test/.../diet/web/`, slice `@WebMvcTest(DietPlanController::class)`.
- [ ] Pokryte scenariusze:
  - POST: `201`, nagłówek `Location` kończący się `/api/v1/diet-plans/{id}`, body z `id/name/kcal/state`.
  - GET lista: `200` i tylko plany bieżącego tenanta.
  - GET po id: `200` dla własnego planu; `404` dla nieistniejącego i dla planu **innego tenanta**.
  - GET z niepoprawnym UUID (`/abc`): `400`.
  - JSON odpowiedzi **nie** zawiera `tenantId`.
- [ ] Tenant w testach ustawiony świadomie: przez `TenantContext` w teście albo przez prawdziwy filtr. Wybierz i uzasadnij przy review. Podpowiedź: `module-diet` nie widzi `TenantFilter` z `app`.
- [ ] `TenantProperties`: testy jednostkowe walidacji sluga (poprawny, z wielką literą, z polskim znakiem, pusty).
- [ ] `FixedTenantResolver`: zwraca tenanta z properties, niezależnie od requestu.
- [ ] Całość `./gradlew test` nadal trwa sekundy (slice, a nie pełny kontekst).

## Wskazówki
- `@WebMvcTest` w Spring Boot 4 może wymagać osobnego modułu testowego (w Boot 4 autokonfiguracje i test slices zostały rozbite na mniejsze moduły). Jeśli adnotacja „nie istnieje”, sprawdź w dokumentacji Boot 4, z którego artefaktu pochodzi.
- `module-diet` nie ma jeszcze `spring-boot-starter-test` (ma tylko `kotlin-test-junit5` z głównego builda). Slice będzie go potrzebował.
- `@WebMvcTest` nie wczytuje Twojego `DietModuleConfig` (to `@Configuration`, a nie `@Controller`), więc beany use case'ów trzeba dostarczyć samemu.
- 500 dla pustej nazwy (brak `@ControllerAdvice`) **nie** jest tu do naprawy, bo to E1. Możesz jednak dodać test, który dokumentuje obecne zachowanie, i oznaczyć go komentarzem `// TODO(E1): expect 400`.

