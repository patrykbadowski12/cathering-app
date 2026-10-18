# Tablica zadań

Statusy: `TODO` · `IN PROGRESS` · `REVIEW` · `CHANGES` · `DONE`
Kto: **Ty** (backend, infrastruktura, chmura; Claude robi review) · **Claude** (frontend; Ty akceptujesz efekt)

## Etap 0 — Fundament (Faza 1)

Cel etapu: stabilna baza (repo, kompilacja, prawdziwa baza danych, migracje, testy, wymuszone granice modułów, szkielet frontu i kontrakt API), zanim zaczniemy dokładać funkcje.

| ID | Zadanie | Kto | Status | Zależy od |
|---|---|---|---|---|
| [T-001](tasks/T-001-repo-i-struktura.md) | Git i porządek w strukturze katalogów | Ty | DONE | — |
| [T-002](tasks/T-002-sciezka-diet-dziala.md) | Projekt się kompiluje, POST/GET `/diet-plans` działa | Ty | DONE | T-001 |
| [T-003](tasks/T-003-testy-domeny-diet.md) | Testy jednostkowe domeny `DietPlan` | Ty | DONE | T-002 |
| [T-004](tasks/T-004-tenant-context.md) | `TenantContext` z domyślnym tenantem z YAML (ADR 0007) | Ty | DONE | T-002 |
| [T-005](tasks/T-005-postgres-docker.md) | PostgreSQL w Docker Compose | Ty | DONE | T-002 |
| [T-006](tasks/T-006-flyway.md) | Flyway: pierwsza migracja i `ddl-auto: validate` | Ty | DONE | T-005 |
| [T-007](tasks/T-007-test-repozytorium-testcontainers.md) | Test integracyjny repozytorium z Testcontainers | Claude | DONE | T-006 |
| [T-008](tasks/T-008-granice-modulow.md) | Decyzja i implementacja: wymuszenie granic modułów | Claude | DONE | T-002 |
| [T-009](tasks/T-009-wspolna-persystencja.md) | `AuditableEntity` we wspólnym miejscu | Ty | TODO | T-008 |
| [T-010](tasks/T-010-ktlint.md) | ktlint i `.editorconfig`: jeden styl, bez wildcard importów | Claude | DONE | — |
| [T-011](tasks/T-011-testy-web-i-konfiguracji.md) | Testy warstwy web (`@WebMvcTest`) i konfiguracji tenanta | Ty | TODO | T-004 |
| [T-012](tasks/T-012-szkielet-frontendu.md) | Szkielet frontendu w `frontend/` (ADR 0011) | Claude | DONE | — |
| [T-013](tasks/T-013-openapi.md) | Kontrakt API: OpenAPI generowane przez backend | Ty | DONE | — |
| [T-014](tasks/T-014-bledy-api-problem-detail.md) | Błędy API jako `ProblemDetail` (400 z polami zamiast 500) | Ty | TODO | — |
| [T-015](tasks/T-015-klient-api-i-pierwszy-ekran.md) | Typowany klient API + pierwszy ekran end-to-end (lista diet) | Claude | REVIEW | T-012, T-013 |
| [T-016](tasks/T-016-jadlospis-w-sklepie.md) | Jadłospis tygodniowy w sklepie (dane przykładowe, kształt API z E2) | Claude | DONE | T-012 |

## Epiki Fazy 1 (pionowe kawałki: backend + frontend; rozpisujemy, gdy dojdziemy)

Każdy epik kończy się czymś, co da się kliknąć. Backend (Ty) dostarcza kontrakt w OpenAPI, frontend (Claude) ekran na jego podstawie.

| Epik | Backend (Ty) | Frontend (Claude) |
|---|---|---|
| E1 — Diety | CRUD, cena jako `Money`, aktywna/nieaktywna, slug do URL-i; endpointy rozdzielone na `/api/v1/shop` i `/api/v1/admin` (ADR 0012) | sklep: lista i szczegóły diety · panel: zarządzanie dietami |
| E2 — Jadłospis | model: dieta → dzień → posiłki, edycja, odczyt na zakres dat | sklep: jadłospis na tydzień · panel: edytor jadłospisu |
| E3 — Zgłoszenie zamówienia | `module-order`: dieta, okres, dni dostaw, dane kontaktowe i adres; `OrderPlacedEvent` | sklep: formularz zamówienia · panel: lista i szczegóły zamówień |
| E4 — Logowanie właściciela | Spring Security, sesja + CSRF, użytkownik powiązany z tenantem; `/api/v1/admin/**` wymaga sesji, tenant użytkownika = tenant z hosta | logowanie, chroniona strefa `/admin` |
| E5 — Lista produkcyjna | zestawienie na dzień: ile jakich posiłków (z zamówień + jadłospisu) | panel: widok dnia, wydruk |
| E6 — CI/CD i deploy | GitHub Actions, obraz Docker, ECR, AWS, deploy frontu (otwarta decyzja w ADR) | build frontu w pipeline |

Kolejność: E4 musi być przed tym, jak panel trafi do kogokolwiek poza nami (najpóźniej przed pierwszym deployem).
