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
- Na Windows sprawdź, czy Docker Desktop działa z backendem WSL2.

## Review
_(uzupełnia Claude)_
