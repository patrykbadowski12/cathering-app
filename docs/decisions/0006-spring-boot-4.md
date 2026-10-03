# 0006 — Spring Boot 4.x zamiast 3.x

**Status:** Accepted · **Data:** 2026-10-03

## Kontekst
`architektura-techniczna.md` zakłada Spring Boot 3.x, a projekt został wygenerowany na 4.1.1 (Kotlin 2.3, Java 21).

## Decyzja
Zostajemy na Spring Boot 4.x (potwierdzone 2026-10-03). To nowy projekt, a linia 3.x zbliża się do końca wsparcia OSS.

## Konsekwencje
- Część tutoriali i odpowiedzi w sieci dotyczy 3.x. W 4.x zmieniły się m.in. pakiety autokonfiguracji (np. `org.springframework.boot.persistence.autoconfigure.EntityScan`). Przy dziwnych błędach w pierwszej kolejności sprawdzaj dokumentację w wersji 4.
