# T-005 — PostgreSQL w Docker Compose

**Status:** DONE · **Etap:** 0 · **Zależy od:** T-002 · **ADR:** 0003 · **Wykonanie:** start własny, dokończenie Claude (zmiany niezacommitowane, do przejrzenia)

## Cel
Aplikacja lokalnie działa na PostgreSQL uruchamianym jednym poleceniem. H2 znika.

## Czego się uczysz
- Docker Compose: serwisy, wolumeny, porty, healthcheck.
- Profile Springa (`application-local.yaml`) i konfiguracja przez zmienne środowiskowe zamiast wpisanych na sztywno haseł.
- Opcjonalnie: wsparcie Spring Boot dla Docker Compose (`spring-boot-docker-compose`), czyli start bazy razem z aplikacją. Porównaj z ręcznym `docker compose up`.

## Kryteria akceptacji
- [ ] `compose.yaml` w katalogu głównym z PostgreSQL (konkretna wersja obrazu, nie `latest`) i wolumenem na dane.
- [ ] `docker compose up -d` i potem `./gradlew :app:bootRun` startuje aplikację na Postgresie.
- [ ] Zależność H2 usunięta, sterownik PostgreSQL dodany.
- [ ] Dane dostępowe nie są wpisane na sztywno w głównym `application.yaml` (profil `local` albo zmienne środowiskowe).
- [ ] POST/GET `/diet-plans` działa, a dane przetrwają restart aplikacji.

## Wskazówki
- Na tym etapie Hibernate jeszcze tworzy schemat (`ddl-auto: update`). To tymczasowe i znika w T-006.
- **Pułapka:** dla H2 Spring Boot domyślnie ustawia `ddl-auto: create-drop`, ale dla „prawdziwej” bazy domyślnie jest `none`. Po przejściu na Postgresa tabele **nie powstaną same**, dopóki nie ustawisz `update` jawnie.
- **Pułapka:** `CateringPlatformApplicationTests.contextLoads` startuje pełny kontekst, więc po usunięciu H2 będzie potrzebował działającej bazy. Między T-005 a T-007 (Testcontainers) `./gradlew build` wymaga `docker compose up`. Zdecyduj świadomie: zaakceptować to przejściowo (i zapisać w README) albo przenieść sam `contextLoads` na Testcontainers już teraz.
- Na Windows sprawdź, czy Docker Desktop działa z backendem WSL2 (`docker compose version` powinno działać w terminalu).
- Nazwy w `compose.yaml` (baza, użytkownik, hasło) i w profilu `local` muszą się zgadzać. Hasło dev w `compose.yaml` jest OK, bo to lokalna baza, ale **nie** w `application.yaml`, który kiedyś trafi na produkcję.
- Healthcheck w compose (`pg_isready`) przyda się później, gdy coś będzie czekać na gotową bazę.
- Sprawdzenie, że dane przetrwały: POST → restart aplikacji (nie kontenera) → GET. Potem `docker compose down` i `up` (bez `-v`), czyli test wolumenu.

## Review

### Diagnoza „nie mogę się połączyć z bazą” — 2026-10-11
1. **Kontener padał przy starcie** (`Exited (1)`). Obraz `postgres:18+` trzyma dane w podkatalogu z numerem wersji i odmawia startu, gdy wolumen jest zamontowany pod starą ścieżką `/var/lib/postgresql/data`. Poprawka: montowanie pod `/var/lib/postgresql`.
2. W `application-local.yaml` były zamienione `username` i `password`.
3. `application.yaml` miał placeholdery `${datasource_url}` itd. Bez aktywnego profilu `local` i bez zmiennych środowiskowych nie było ich czym zastąpić.
4. Tenant był tylko w profilu `local`, więc bez tego profilu walidacja z T-004 blokowała start (zgodnie z zamierzeniem).
5. `ddl-auto: create` kasował dane przy każdym starcie.
6. Praca była na `main`, a na `main` po merge'u T-010 siedziała zbędna linia `git` w `settings.gradle.kts`, przez co build nie przechodził.

### Dokończenie — 2026-10-11 · gałąź `task/T-005-postgres` → **REVIEW** (czeka na Twój przegląd przed commitem)

**Zmiany:**
- `docker-compose.yaml` → `compose.yaml`:
  - `postgres:18.6-alpine`, port `5432`,
  - **nazwany wolumen** `postgres-data` pod `/var/lib/postgresql` zamiast katalogu `./db-data` w repo,
  - healthcheck `pg_isready`,
  - bez przestarzałego `version:`.
- `application.yaml`:
  - bez danych dostępowych (komentarz: profil `local` albo zmienne `SPRING_DATASOURCE_*`, które Spring wiąże sam, bez własnych placeholderów),
  - `ddl-auto: update` z `TODO(T-006)`,
  - tenant przywrócony,
  - usunięte zbędne `driver-class-name` i `database-platform` (Boot wykrywa je z URL-a).
- `application-local.yaml`: URL, użytkownik i hasło zgodne z `compose.yaml`.
- `app/build.gradle.kts`:
  - H2 usunięte (Twoja zmiana) ✅,
  - sterownik `postgresql` jako `runtimeOnly`, bo kod go nie importuje,
  - `bootRun` domyślnie z profilem `local` (nadpisywalnym).
- `CateringPlatformApplicationTests`: `@ActiveProfiles("local")` + `TODO(T-007)`. **Świadoma decyzja:** do T-007 build wymaga `docker compose up`. Testcontainers to temat T-007, którego nie chciałem Ci zabierać.
- `settings.gradle.kts`: usunięta zbędna linia `git` (Twoja zmiana) ✅.
- `docs/README.md`: sekcja „Uruchomienie lokalne” (compose, `bootRun`, profil w IntelliJ, wymaganie bazy dla testów).

**Weryfikacja:**

| Kryterium | Wynik |
|---|---|
| `compose.yaml` z konkretną wersją obrazu i wolumenem | ✅ `Up (healthy)` |
| `docker compose up -d` + `./gradlew :app:bootRun` → aplikacja na Postgresie | ✅ log: `The following 1 profile is active: "local"`, `Database version: 18.6` |
| H2 usunięte, sterownik PostgreSQL dodany | ✅ |
| Brak danych dostępowych w `application.yaml` | ✅ |
| POST/GET działa, dane przetrwają restart | ✅ POST `201`, potem restart aplikacji **i** `docker compose down` + `up` (bez `-v`), a GET pod adres z `Location` → `200` z tym samym planem |
| `./gradlew build` | ✅ 19 testów; `contextLoads` łączy się z `jdbc:postgresql://localhost:5432/catering_database` |

### Zamknięcie — 2026-10-11 → **DONE**
Zmergowane w PR #5.
