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
| [T-009](tasks/T-009-wspolna-persystencja.md) | `AuditableEntity` we wspólnym miejscu (odłożone do startu E3) | Ty | TODO | E3 |
| [T-010](tasks/T-010-ktlint.md) | ktlint i `.editorconfig`: jeden styl, bez wildcard importów | Claude | DONE | — |
| [T-011](tasks/T-011-testy-web-i-konfiguracji.md) | Testy warstwy web (`@WebMvcTest`) i konfiguracji tenanta | Ty | DONE | T-004, T-014 |
| [T-012](tasks/T-012-szkielet-frontendu.md) | Szkielet frontendu w `frontend/` (ADR 0011) | Claude | DONE | — |
| [T-013](tasks/T-013-openapi.md) | Kontrakt API: OpenAPI generowane przez backend | Ty | DONE | — |
| [T-014](tasks/T-014-bledy-api-problem-detail.md) | Błędy API jako `ProblemDetail` (400 z polami zamiast 500) | Ty | DONE | — |
| [T-015](tasks/T-015-klient-api-i-pierwszy-ekran.md) | Typowany klient API + pierwszy ekran end-to-end (lista diet) | Claude | DONE | T-012, T-013 |
| [T-016](tasks/T-016-jadlospis-w-sklepie.md) | Jadłospis tygodniowy w sklepie (dane przykładowe, kształt API z E2) | Claude | DONE | T-012 |

## Etap A / E1 — Diety (ADR 0014)

Cel: dieta z wariantami kalorycznymi i ceną zastępuje `DietPlan`; sklep pokazuje ceny i szczegóły diety, panel pozwala nią zarządzać. Stary i nowy model żyją obok siebie do T-024 (*expand → migrate → contract*), więc `main` cały czas działa. Kolejność: [roadmap.md](roadmap.md).

| ID | Zadanie | Kto | Status | Zależy od |
|---|---|---|---|---|
| [T-018](tasks/T-018-agregat-diety.md) | Agregat `Diet` z wariantami i `Money` (domena) | Ty | DONE | — |
| [T-019](tasks/T-019-persystencja-diety.md) | Persystencja agregatu `Diet` (migracja + JPA) | Ty | TODO | T-018 |
| [T-020](tasks/T-020-api-panelu-diety.md) | API panelu: zarządzanie dietami i wariantami | Ty | TODO | T-019 |
| [T-021](tasks/T-021-api-sklepu-diety.md) | API sklepu: aktywne diety i szczegóły po slugu | Ty | TODO | T-019 |
| [T-022](tasks/T-022-sklep-diety-i-szczegoly.md) | Sklep: karty diet z ceną i strona szczegółów | Claude | TODO | T-021 |
| [T-023](tasks/T-023-panel-zarzadzanie-dietami.md) | Panel: zarządzanie dietami i wariantami | Claude | TODO | T-020 |
| [T-024](tasks/T-024-usuniecie-diet-plan.md) | Usunięcie starego modelu `DietPlan` | Ty | TODO | T-022, T-023 |
| [T-017](tasks/T-017-ci-github-actions.md) | CI: GitHub Actions dla backendu i frontendu | Ty | TODO | — |

## Epiki (pionowe kawałki: backend + frontend; rozpisujemy, gdy dojdziemy)

Kolejność i uzasadnienie: [roadmap.md](roadmap.md).

Każdy epik kończy się czymś, co da się kliknąć. Backend (Ty) dostarcza kontrakt w OpenAPI, frontend (Claude) ekran na jego podstawie.

| Epik | Backend (Ty) | Frontend (Claude) |
|---|---|---|
| E1 — Diety | agregat `Diet` z wariantami (ADR 0014), cena jako `Money`, aktywna/nieaktywna, slug do URL-i; endpointy w `/api/v1/shop` i `/api/v1/admin` | sklep: lista i szczegóły diety z wyborem kaloryczności · panel: zarządzanie dietami i wariantami |
| E2 — Jadłospis | model: dieta → dzień → posiłki, edycja, odczyt na zakres dat | sklep: jadłospis na tydzień · panel: edytor jadłospisu |
| E3 — Zapytania | zapis zapytania (wariant, kontakt, preferencje), mail do właściciela, ochrona przed botami (honeypot, limit z IP) | sklep: formularz zapytania · panel: lista zapytań ze statusem |
| E4 — Logowanie właściciela | Spring Security, sesja + CSRF, użytkownik powiązany z tenantem; `/api/v1/admin/**` wymaga sesji, tenant użytkownika = tenant z hosta | logowanie, chroniona strefa `/admin` |
| E5 — Zamówienia w panelu | `module-order`: właściciel wprowadza ustalone zamówienie (zwykle z zapytania); model do ustalenia przed E3 | panel: tworzenie i edycja zamówień, lista |
| E6 — Lista produkcyjna | zestawienie na dzień: ile jakich posiłków (z zamówień + jadłospisu) | panel: widok dnia, wydruk |
| E7 — Deploy | obraz Docker, ECR, AWS, wysyłka maili (SES), domena, deploy frontu (otwarta decyzja w ADR); CI wcześniej w T-017 | build frontu w pipeline |
| E8 — Powiadomienia | outbox (ADR 0004), mail/SMS do właściciela, przypomnienie klientowi o końcu diety | panel: ustawienia powiadomień |
| E9 — Samoobsługa klienta | link z maila do zamówienia, przerwy z terminem zgłoszenia | strona zarządzania zamówieniem |
| E10 — Obserwowalność | logi strukturalne, metryki, tracing, alerty | — |

Kolejność: E4 musi być przed tym, jak panel trafi do kogokolwiek poza nami (najpóźniej przed pierwszym deployem).
