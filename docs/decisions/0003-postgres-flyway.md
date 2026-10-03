# 0003 — PostgreSQL i Flyway od początku

**Status:** Accepted · **Data:** 2026-10-03 · **Wdrożenie:** T-005, T-006

## Kontekst
Teraz jest H2 in-memory, a schemat generuje Hibernate (`ddl-auto`). H2 różni się od PostgreSQL (typy, `jsonb`, RLS), a schemat generowany automatycznie nie nadaje się na produkcję ani na migracje danych w Fazie 2 (dodanie `tenant_id`).

## Decyzja
- PostgreSQL lokalnie (Docker Compose), w testach (Testcontainers) i na produkcji (RDS).
- Schemat **wyłącznie** przez migracje Flyway. Hibernate tylko waliduje (`ddl-auto: validate`).
- H2 znika z projektu.

## Konsekwencje
- Lokalnie potrzebny jest Docker.
- Każda zmiana encji wymaga migracji, co jest celowym „tarciem”.
- Testy działają na tej samej bazie co produkcja, więc nie ma niespodzianek.
