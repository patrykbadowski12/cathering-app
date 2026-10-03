# Tablica zadań

Statusy: `TODO` · `IN PROGRESS` · `REVIEW` · `CHANGES` · `DONE`

## Etap 0 — Fundament (Faza 1)

Cel etapu: stabilna baza (repo, kompilacja, prawdziwa baza danych, migracje, testy, wymuszone granice modułów), zanim zaczniemy dokładać funkcje.

| ID | Zadanie | Status | Zależy od |
|---|---|---|---|
| [T-001](tasks/T-001-repo-i-struktura.md) | Git i porządek w strukturze katalogów | TODO | — |
| [T-002](tasks/T-002-sciezka-diet-dziala.md) | Projekt się kompiluje, POST/GET `/diet-plans` działa | TODO | T-001 |
| [T-003](tasks/T-003-testy-domeny-diet.md) | Testy jednostkowe domeny `DietPlan` | TODO | T-002 |
| [T-004](tasks/T-004-tenant-context.md) | Decyzja i implementacja: skąd bierze się `tenantId` | TODO | T-002 |
| [T-005](tasks/T-005-postgres-docker.md) | PostgreSQL w Docker Compose | TODO | T-002 |
| [T-006](tasks/T-006-flyway.md) | Flyway: pierwsza migracja i `ddl-auto: validate` | TODO | T-005 |
| [T-007](tasks/T-007-test-repozytorium-testcontainers.md) | Test integracyjny repozytorium z Testcontainers | TODO | T-006 |
| [T-008](tasks/T-008-granice-modulow.md) | Decyzja i implementacja: wymuszenie granic modułów | TODO | T-002 |
| [T-009](tasks/T-009-wspolna-persystencja.md) | `AuditableEntity` we wspólnym miejscu | TODO | T-008 |

## Epiki na później (rozpisujemy, gdy dojdziemy)

| Epik | Zakres | Faza |
|---|---|---|
| E1 — Katalog diet | Pełny CRUD, cena jako `Money`, walidacja, obsługa błędów (`@ControllerAdvice`) | 1 |
| E2 — Jadłospis | Dni, posiłki, powiązanie z dietą | 1 |
| E3 — Zamówienia | `module-order`, koszyk, zamówienie, `OrderPlacedEvent` | 1 |
| E4 — Panel właściciela | Thymeleaf, Spring Security, logowanie | 1 |
| E5 — CI/CD i deploy | GitHub Actions, Docker image, ECR, AWS | 1 |
