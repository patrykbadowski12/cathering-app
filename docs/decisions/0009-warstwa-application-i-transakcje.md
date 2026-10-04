# 0009 — Warstwa `application` i granica transakcji

**Status:** Accepted · **Data:** 2026-10-04 · **Doprecyzowuje:** 0005 · **Wdrożenie:** T-002

## Kontekst
ADR 0005 mówi, że domena nie zna Springa. Use case'y leżały w `domain`, więc nie było miejsca na `@Transactional`. Rozważane opcje:
- **Kontroler:** odrzucone. Web to tylko jedno wejście. `@EventListener`, `@Scheduled` i konsumenci SQS ominęliby transakcję, a transakcja objęłaby też serializację odpowiedzi.
- **Własny aspekt:** odrzucone, bo to ręczna re-implementacja `@Transactional`.
- **Port `TransactionRunner`** (aplikacja bez Springa): odrzucone. Każdy use case musiałby jawnie owijać kod, a `@TransactionalEventListener` (E3) i tak opiera się na transakcjach Springa.
- **Warstwa `application` z `@Transactional`:** wybrane.

## Decyzja
Każdy moduł ma cztery warstwy:

| Pakiet | Zawartość | Może zależeć od |
|---|---|---|
| `domain` | agregaty, value objects, porty (np. repozytoria), reguły biznesowe | nic (czysty Kotlin) |
| `application` | use case'y: orkiestracja, **granica transakcji** | `domain` + **wyłącznie** `spring-tx` (`@Transactional`) |
| `infrastructure` | adaptery (JPA), `*ModuleConfig` | `domain`, `application`, Spring |
| `web` | kontrolery, DTO request/response | `application`, `domain` (typy), Spring Web |

- `@Transactional` stawiamy na klasie use case'u. Odczyty dostają `@Transactional(readOnly = true)`.
- **Jeden use case = jedna transakcja = zmiana jednego agregatu.** Skutki w innych modułach wywołujemy przez zdarzenia po commicie (`@TransactionalEventListener`), a nie przez wspólną transakcję.

## Konsekwencje
- Każde wejście (web, zdarzenie, scheduler) dostaje poprawną transakcję, bo woła use case.
- Proxy transakcyjne wymaga klas `open`. Zapewnia to plugin `kotlin("plugin.spring")`, który otwiera klasy z `@Transactional`. Usunięcie pluginu po cichu wyłączy transakcje.
- `@Transactional` działa tylko przy wywołaniu **przez proxy**. Wywołanie metody z tej samej klasy (`this.x()`) omija transakcję.
- Reguły zależności między warstwami trafiają do automatycznej weryfikacji w T-008.
