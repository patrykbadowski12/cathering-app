# T-005 — PostgreSQL w Docker Compose

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-002 · **ADR:** 0003

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
_(uzupełnia Claude)_
