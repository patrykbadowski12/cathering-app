# 0004 — Komunikacja między modułami: publiczne API i zdarzenia domenowe

**Status:** Accepted · **Data:** 2026-10-03

## Kontekst
Gdy moduł A sięga do repozytorium albo encji modułu B, powstaje ukryte sprzężenie, które uniemożliwia późniejszy podział.

## Decyzja
- Moduł może używać innego modułu **tylko** przez jego publiczne API (interfejsy i typy z `domain`). Nigdy przez `infrastructure`.
- Reakcje na zdarzenia z innego modułu idą przez `DomainEvent` publikowane przez `ApplicationEventPublisher` i słuchane przez `@EventListener` (później `@TransactionalEventListener`).
- Przy wydzieleniu serwisu podmieniamy tylko adapter publikujący (na SQS/SNS).

## Konsekwencje
- Więcej „ceremonii” niż bezpośrednie wywołanie.
- Do przemyślenia przy E3: co, jeśli aplikacja padnie między commitem a obsługą zdarzenia? (hasło: *transactional outbox*).
