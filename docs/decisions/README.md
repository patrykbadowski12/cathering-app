# Decyzje architektoniczne (ADR)

Format: krótki kontekst, decyzja i konsekwencje. Wzorzec: Michael Nygard, „Documenting Architecture Decisions”.

## Podjęte

| ADR | Decyzja | Status |
|---|---|---|
| [0001](0001-modularny-monolit.md) | Modularny monolit, moduł Gradle per bounded context | Accepted |
| [0002](0002-spring-mvc-blocking.md) | Spring MVC (blocking) i `ThreadLocal`, bez WebFlux/korutyn w Fazach 1–3 | Accepted |
| [0003](0003-postgres-flyway.md) | PostgreSQL i Flyway od początku | Accepted (wdrożenie: T-005, T-006) |
| [0004](0004-komunikacja-przez-zdarzenia.md) | Moduły komunikują się przez publiczne API i zdarzenia domenowe | Accepted |
| [0005](0005-domena-bez-springa.md) | Domena bez zależności od Springa/JPA (porty i adaptery) | Accepted (doprecyzowany przez 0009) |
| [0006](0006-spring-boot-4.md) | Spring Boot 4.x zamiast 3.x z pierwotnego planu | Accepted |
| [0007](0007-zrodlo-tenant-id.md) | `tenantId`: Filter → `TenantResolver` → `TenantContext`, przekazywany jawnie do domeny; Faza 1 = domyślny tenant z YAML | Accepted (wdrożenie: T-004) |
| [0008](0008-uuid-v7-i-slug.md) | Tożsamość: UUIDv7 w typach domenowych. Czytelność: `slug` (tenant w MDC, diety w URL-ach) | Accepted (wdrożenie: T-004, T-006) |
| [0009](0009-warstwa-application-i-transakcje.md) | Warstwa `application` (use case'y) jako granica transakcji; `@Transactional` jedyną zależnością od Springa | Accepted (wdrożenie: T-002) |
| [0010](0010-migracje-w-modulach.md) | Migracje Flyway w module-właścicielu, wersje datowe `V<yyyy>_<MM>_<dd>_<HHmm>__` | Accepted (wdrożenie: T-006) |
| [0011](0011-frontend-spa-react.md) | Frontend: SPA React + TypeScript w `frontend/`, ten sam origin co API, klient generowany z OpenAPI; SSR sklepu (Next.js) odłożone | Accepted |
| [0012](0012-uklad-adresow-sklep-panel-api.md) | Jedna domena per tenant: `/` sklep, `/admin` panel, API rozdzielone na `/api/v1/shop` (publiczne) i `/api/v1/admin` (sesja właściciela) | Accepted |
| [0013](0013-granice-modulow-archunit.md) | Granice modułów: Gradle (zależności modułów) + testy ArchUnit (warstwy, dostęp między modułami, czysty shared kernel) | Accepted (wdrożenie: T-008) |

## Otwarte decyzje

| Pytanie | Opcje | Rozstrzygamy w |
|---|---|---|
| Rozpoznawanie tenanta w Fazie 2 (host, własne domeny, panel, proxy) | kierunek opisany w ADR 0007, do potwierdzenia | Faza 2 |
| Gdzie żyje kod wspólny dla persystencji (`AuditableEntity`)? | `shared-kernel` / osobny moduł `shared-infrastructure` | T-009 |
| Deploy frontu: pliki serwowane przez Spring czy S3 + CloudFront? | ADR 0011 zostawia otwarte | E6 (CI/CD) |
