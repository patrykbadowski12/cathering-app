# T-006 — Flyway: pierwsza migracja i `ddl-auto: validate`

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-005 · **ADR:** 0003

## Cel
Schemat bazy powstaje wyłącznie z migracji Flyway, a Hibernate tylko sprawdza zgodność encji ze schematem.

## Czego się uczysz
- Wersjonowane migracje: `V1__...sql`, tabela `flyway_schema_history`, dlaczego **nigdy** nie edytuje się zastosowanej migracji.
- Jawne projektowanie schematu: typy, `NOT NULL`, indeksy, nazwy constraintów.

## Kryteria akceptacji
- [ ] Zależności Flyway dodane (dla PostgreSQL potrzebny jest dodatkowy moduł bazy danych Flyway, sprawdź dokumentację).
- [ ] `V1__create_diet_plans.sql` tworzy tabelę `diet_plans` z kolumnami audytu i `tenant_id NOT NULL` (ADR 0007).
- [ ] Indeks pod zapytanie „plany danego tenanta”.
- [ ] `spring.jpa.hibernate.ddl-auto: validate`, a aplikacja startuje.
- [ ] Eksperyment: zmień nazwę pola w encji i zobacz, co mówi `validate`. Potem wycofaj zmianę, a potem powiedz przy review, co zobaczyłeś.

## Wskazówki
- Zastanów się nad typami: `kcal` jako `integer`, a `state` jako `varchar` czy natywny `enum` PostgreSQL? Ma to wpływ na przyszłe migracje.
- Gdzie trzymać migracje: w `app` czy w module, którego dotyczą? Każda opcja ma konsekwencje dla przyszłego podziału na serwisy (ADR 0001). Warto o tym pogadać.

## Review
_(uzupełnia Claude)_
