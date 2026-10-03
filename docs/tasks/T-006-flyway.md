# T-006 — Flyway: pierwsza migracja i `ddl-auto: validate`

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-005 · **ADR:** 0003, 0008

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
- [ ] Nowe `DietPlanId` są generowane jako **UUIDv7** (ADR 0008), w jednym miejscu (np. funkcja w `shared-kernel`), a nie przez `UUID.randomUUID()` rozsiane po domenie.
- [ ] Eksperyment: zmień nazwę pola w encji i zobacz, co mówi `validate`. Potem wycofaj zmianę i powiedz przy review, co zobaczyłeś.

## Wskazówki
- Zastanów się nad typami: `kcal` jako `integer`, a `state` jako `varchar` czy natywny `enum` PostgreSQL? Ma to wpływ na przyszłe migracje.
- UUIDv7: standardowe `UUID.randomUUID()` daje v4. Opcje: biblioteka (np. `com.fasterxml.uuid:java-uuid-generator`, `com.github.f4b6a3:uuid-creator`) albo własna funkcja wg RFC 9562. Domena generuje ID sama (`DietPlan.create`), więc generatory Hibernate/PostgreSQL tu nie pomogą. Dlaczego?
- Gdzie trzymać migracje: w `app` czy w module, którego dotyczą? Każda opcja ma konsekwencje dla przyszłego podziału na serwisy (ADR 0001). Warto o tym pogadać.

## Review
_(uzupełnia Claude)_
