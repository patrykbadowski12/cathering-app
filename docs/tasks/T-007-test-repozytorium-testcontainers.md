# T-007 — Test integracyjny repozytorium z Testcontainers

**Status:** DONE · **Etap:** 0 · **Zależy od:** T-006 · **Wykonanie:** Claude (na prośbę)

## Cel
Adapter `DietPlanRepositoryImpl` jest przetestowany na prawdziwym PostgreSQL, z migracjami Flyway uruchamianymi w teście.

## Czego się uczysz
- Piramida testów: co testujemy jednostkowo (T-003), a co integracyjnie.
- Testcontainers: kontener bazy na czas testów, `@ServiceConnection` w Spring Boot.
- Test slice `@DataJpaTest` vs pełny `@SpringBootTest`: koszt i zakres.

## Kryteria akceptacji
- [ ] Test zapisuje `DietPlan` przez port `DietPlanRepository` i odczytuje go z powrotem (round-trip mapowania).
- [ ] Test `findAllByTenantId` potwierdza, że plany innego tenanta nie wracają.
- [ ] Test używa Flyway (bez `ddl-auto: create`).
- [ ] Test `findByIdAndTenantId` na prawdziwej bazie: plan innego tenanta → `null`.
- [ ] Odczyt w teście round-trip naprawdę idzie do bazy (`flush()` + `clear()` przed odczytem; w logu `insert` i `select`).
- [ ] `CateringPlatformApplicationTests.contextLoads` też działa na Testcontainers, **bez** `@ActiveProfiles("local")`.
- [ ] `./gradlew build` przechodzi przy włączonym Dockerze **i wyłączonym** `docker compose`.
- [ ] Obraz Postgresa w testach = obraz z `compose.yaml`.
- [ ] `docs/README.md` zaktualizowany: testy nie wymagają już `docker compose up`, ale wymagają Dockera.

## Wskazówki
- **Boot 4 = Testcontainers 2.x.** Artefakty mają prefiks `testcontainers-` (`org.testcontainers:testcontainers-postgresql`, `…:testcontainers-junit-jupiter`), a klasa to `org.testcontainers.postgresql.PostgreSQLContainer`. Przykłady z 1.x (`org.testcontainers:postgresql`) nie pasują. Do tego `org.springframework.boot:spring-boot-testcontainers`. Wersje sprawdź w BOM-ie Boota (`./gradlew :app:dependencyInsight …`).
- Kontener startuje kilka sekund. Sprawdź, jak go współdzielić między klasami testowymi zamiast stawiać nowy dla każdej: `@TestConfiguration` z `@Bean @ServiceConnection` albo `@ImportTestcontainers`. `app` i `module-diet` to osobne JVM-y testowe, więc każdy moduł i tak ma własny kontener.
- **Pułapki `@DataJpaTest` w `module-diet`:**
  1. Brak `@SpringBootConfiguration` w module, bo klasa aplikacji jest w `app`. Potrzebna jest testowa klasa konfiguracyjna w `module-diet/src/test`.
  2. Slice nie ładuje Twoich klas `@Repository`, więc `DietPlanRepositoryImpl` trzeba dodać przez `@Import`.
  3. Audyt (`@EnableJpaAuditing`, `AuditorAware`) jest w `app`. Bez niego `created_by NOT NULL` zablokuje zapis.
  4. First-level cache: `save()` + `find()` w jednej transakcji nie dotyka bazy, dopóki nie zrobisz `flush()` i `clear()`.
  5. `@AutoConfigureTestDatabase` domyślnie podmienia datasource na bazę wbudowaną. (W praktyce przy `@ServiceConnection` nie było to potrzebne.)
  6. W Boot 4 test slices są w osobnych artefaktach i pakietach. Jeśli import `@DataJpaTest` nie istnieje, sprawdź dokumentację Boot 4.

## Review

### Wykonanie — 2026-10-12 · gałąź `task/T-007-testcontainers` → **REVIEW** (wykonał Claude na prośbę; niezacommitowane, do Twojego przeglądu)

**Zmiany:**
- `app/build.gradle.kts`: `org.testcontainers:postgresql` (nazwa z 1.x, nie rozwiązywała się: `FAILED`) → `org.testcontainers:testcontainers-postgresql`. Usunięte zbędne `testcontainers-junit-jupiter`, bo kontener jest beanem Springa, a nie polem z `@Container`. Wersje (Testcontainers 2.0.5) z BOM-u Boota.
- `app`: `TestcontainersConfiguration` (`@TestConfiguration` + `@Bean @ServiceConnection PostgreSQLContainer("postgres:18.6-alpine")`). `CateringPlatformApplicationTests` dostał `@Import` tej konfiguracji, a `@ActiveProfiles("local")` i `TODO(T-007)` zostały usunięte.
- `module-diet/build.gradle.kts`: zależności testowe:
  - `spring-boot-starter-test`,
  - `spring-boot-data-jpa-test` (w Boot 4 `@DataJpaTest` jest w osobnym artefakcie, pakiet `org.springframework.boot.data.jpa.test.autoconfigure`),
  - `spring-boot-testcontainers`, `spring-boot-starter-flyway`, `testcontainers-postgresql`,
  - sterowniki jako `testRuntimeOnly`.
- `module-diet/src/test`:
  - `DietTestApplication`: `@SpringBootConfiguration` + `@AutoConfigurationPackage` (wskazuje pakiet encji i repozytoriów bez skanowania kontrolerów i `DietModuleConfig`) + `@EnableJpaAuditing` z testowym `AuditorAware`,
  - własna `TestcontainersConfiguration`,
  - `DietPlanRepositoryImplTest` (zastąpił pusty szkic `DietPlanJpaTest`).
- `docs/README.md`: testy nie wymagają `docker compose`, tylko Dockera.

**Testy w `DietPlanRepositoryImplTest`** (`@DataJpaTest` + `@Import(DietPlanRepositoryImpl, TestcontainersConfiguration)`):

| Test | Co pilnuje |
|---|---|
| zapis i odczyt po `flush()` + `clear()` | mapowanie encja ↔ domena na prawdziwym SQL, a nie z cache JPA |
| zapis nowego planu = **1** zapytanie (statystyki Hibernate) | `Persistable.isNew()`: `persist` zamiast `merge`, bez `SELECT` przed `INSERT` |
| kolumny audytu wypełnione | `@EnableJpaAuditing` + `created_by NOT NULL` z migracji |
| `findAllByTenantId` zwraca dokładnie zbiór planów tenanta | izolacja tenantów (porównanie zbiorów ID, niezależne od kolejności) |
| `findByIdAndTenantId` dla planu innego tenanta → `null` | izolacja tenantów przy odczycie po ID |
| nieistniejące ID → `null` | |

**Weryfikacja:**
- `./gradlew clean build` ✅ **30 testów** (shared-kernel 7, module-diet 20, app 3), ~40 s, przy **wyłączonym** `docker compose`. Każdy moduł stawia własny kontener na losowym porcie (`localhost:50756`, `localhost:61613`).
- Flyway w obu kontenerach: `Successfully applied 1 migration … v2026.10.11.1200`, Postgres 18.6.
- Mutacje (wycofane):
  - `isNew()` zawsze `false` → pada „1 zapytanie przy zapisie”,
  - `findByIdAndTenantId` z `@Query` bez warunku na tenanta → pada „plan innego tenanta → `null`”.
- ktlint ✅.

| Kryterium | Wynik |
|---|---|
| Round-trip przez port | ✅ |
| `findAllByTenantId` bez planów innego tenanta | ✅ |
| Flyway w teście (bez `ddl-auto: create`) | ✅ |
| `findByIdAndTenantId` → `null` dla innego tenanta | ✅ |
| Odczyt idzie do bazy (`flush` + `clear`) | ✅ |
| `contextLoads` na Testcontainers, bez profilu `local` | ✅ |
| Build bez `docker compose` | ✅ |
| Ten sam obraz co w `compose.yaml` | ✅ `postgres:18.6-alpine` |
| README | ✅ |

**Warto wiedzieć:**
- **Bez działającego Dockera testy integracyjne padają** z komunikatem `Could not find a valid Docker environment`. Tak było przy pierwszym czystym buildzie, bo Docker Desktop był wyłączony. To nie błąd w kodzie: uruchom Docker Desktop. Testy jednostkowe (`shared-kernel`, `application`, `domain`) przechodzą zawsze.
- `TestcontainersConfiguration` jest skopiowana w `app` i `module-diet` (osobne JVM-y testowe, moduły nie widzą nawzajem swoich testów). Przy trzecim module warto przenieść ją do `java-test-fixtures`.
- Tag obrazu `postgres:18.6-alpine` jest w trzech miejscach (`compose.yaml` + dwie konfiguracje testowe). Przy podbijaniu wersji zmień wszystkie.

**Do posprzątania poza tym zadaniem:** `module-diet` i `module-order` nadal mają `spring-boot-starter-thymeleaf`, a po ADR 0011 nie ma już szablonów po stronie serwera. Do usunięcia, np. w T-013.

### Zamknięcie — 2026-10-12 → **DONE**
Zmergowane w PR #7.
