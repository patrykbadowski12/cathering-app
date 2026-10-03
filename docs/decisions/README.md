# Decyzje architektoniczne (ADR)

Format: krótki kontekst, decyzja i konsekwencje. Wzorzec: Michael Nygard, „Documenting Architecture Decisions”.

## Podjęte

| ADR | Decyzja | Status |
|---|---|---|
| [0001](0001-modularny-monolit.md) | Modularny monolit, moduł Gradle per bounded context | Accepted |
| [0002](0002-spring-mvc-blocking.md) | Spring MVC (blocking) i `ThreadLocal`, bez WebFlux/korutyn w Fazach 1–3 | Accepted |
| [0003](0003-postgres-flyway.md) | PostgreSQL i Flyway od początku | Accepted (wdrożenie: T-005, T-006) |
| [0004](0004-komunikacja-przez-zdarzenia.md) | Moduły komunikują się przez publiczne API i zdarzenia domenowe | Accepted |
| [0005](0005-domena-bez-springa.md) | Domena bez zależności od Springa/JPA (porty i adaptery) | Accepted |
| [0006](0006-spring-boot-4.md) | Spring Boot 4.x zamiast 3.x z pierwotnego planu | Accepted |
| [0007](0007-zrodlo-tenant-id.md) | `tenantId`: Filter → `TenantResolver` → `TenantContext`, przekazywany jawnie do domeny; Faza 1 = domyślny tenant z YAML | Accepted (wdrożenie: T-004) |

## Otwarte decyzje

| Pytanie | Opcje | Rozstrzygamy w |
|---|---|---|
| Rozpoznawanie tenanta w Fazie 2 (host, własne domeny, panel, proxy) | kierunek opisany w ADR 0007, do potwierdzenia | Faza 2 |
| Jak wymusić granice modułów? | Gradle submoduły `domain/infra/web` / ArchUnit / Spring Modulith | T-008 |
| Gdzie żyje kod wspólny dla persystencji (`AuditableEntity`)? | `shared-kernel` / osobny moduł `shared-infrastructure` | T-009 |
| REST + osobny frontend czy Thymeleaf SSR dla sklepu? | plan mówi Thymeleaf; `DietPlanController` jest REST | E1/E4 |
