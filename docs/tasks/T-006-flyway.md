# T-006 — Flyway: pierwsza migracja i `ddl-auto: validate`

**Status:** DONE · **Etap:** 0 · **Zależy od:** T-005 · **ADR:** 0003, 0008, 0010

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

### Runda 1 — 2026-10-11 · gałąź `task/T-006-flyway` → **CHANGES** (drobne)

**Jak sprawdzone:** na **osobnym, tymczasowym** kontenerze Postgres (port 55432; Twojej lokalnej bazy nie ruszałem):
- `./gradlew build` ✅ 19 testów. W logu `contextLoads`: `Migrating schema "public" to version "2026.10.11.1200 - create diet plans"` → `Successfully applied 1 migration`, a Hibernate `validate` akceptuje schemat.
- `bootRun` + 2× POST → `201`. W bazie: oba ID mają wersję **7**, a `ORDER BY id` zwraca je w kolejności utworzenia (sortowalność v7 działa).
- `flyway_schema_history`: `2026.10.11.1200 | create diet plans | success = t`.
- Ręczne `INSERT`-y z `kcal = 0`, nazwą `'   '` i stanem `'DELETED'` zostały odrzucone przez `ck_diet_plans_kcal_positive`, `ck_diet_plans_name_not_blank` i `ck_diet_plans_state`.

**Co jest dobre (Twoje zmiany):**
- Migracje w `module-diet` z wersją datową (→ zapisane jako ADR 0010).
- `baseline-on-migrate` i zbędne domyślne ustawienia Flyway usunięte.
- `ddl-auto: validate`.
- UUIDv7 przez bibliotekę opakowaną w **jedno** miejsce (`Ids.uuidV7()`), z zależnością `implementation` w `shared-kernel`, więc moduły nie widzą biblioteki bezpośrednio.
- Porządek w `shared-kernel` (pakiety `tenant` i `id`).
- Starter Flyway + `flyway-database-postgresql` (Boot 4).

**Poprawiony przez Claude skrypt migracji** (na prośbę). Zmiana nazwy pliku na `V2026_10_11_1200__create_diet_plans.sql`, czyli z godziną (ADR 0010) i liczbą mnogą jak tabela.

| Było | Jest | Dlaczego |
|---|---|---|
| `TIMESTAMP` | `TIMESTAMPTZ` | pola `Instant` to punkt w czasie (strefa ma znaczenie) |
| `created_by VARCHAR`, nullable | `VARCHAR(255) NOT NULL` | audytor zawsze coś ustawia |
| `created_on … DEFAULT now()` | bez `DEFAULT` | wartość ustawia audyt Springa. Dwa źródła prawdy dla tej samej kolumny to proszenie się o rozjazd |
| `PRIMARY KEY NOT NULL`, `CHECK` bez nazw | nazwane `pk_` / `ck_` | czytelne błędy (patrz wyżej), łatwe `ALTER` w przyszłości |
| — | `ck_diet_plans_name_not_blank` | ta sama reguła co `isNotBlank()` w domenie |

**Do zrobienia (blokujące, drobne):**
1. **Lokalna baza:** ma jeszcze tabelę utworzoną przez Hibernate w T-005 i nie ma historii Flyway, więc aplikacja się nie uruchomi (*non-empty schema without schema history table*). Wyczyść ją: `docker compose down -v`, potem `docker compose up -d`.
2. **`Ids.uuidV7()` bez typu zwracanego.** `UuidCreator.getTimeOrderedEpoch()` to metoda Javy, więc typ jest platformowy (`UUID!`). Dopisz `: UUID`, tak jak w `TenantContext.get()`.
3. **Test `Ids`** w `shared-kernel`: `uuidV7().version() == 7` oraz dwa kolejne ID są rosnące (`compareTo`).
4. **Eksperyment z kryteriów:** zmień nazwę pola w encji, zobacz komunikat `validate` przy starcie, wycofaj zmianę i opowiedz, co zobaczyłeś.

**Drobne (nie blokują):**
- `flyway-database-postgresql` może być `runtimeOnly` (kod go nie importuje), tak jak sterownik PostgreSQL.
- `name VARCHAR(200)`: domena nie ma limitu długości, więc nazwa z 201 znakami skończy się błędem bazy (500), a nie czytelną walidacją. Dodaj regułę w `DietPlan.create()` teraz albo w E1 razem z `@ControllerAdvice`.

### Runda 2 — 2026-10-11 · poprawki dociągnięte przez Claude (na prośbę) → **REVIEW** (niezacommitowane, do Twojego przeglądu)

**Zmiany:**
- `Ids.uuidV7(): UUID`: jawny typ zamiast platformowego `UUID!`, plus KDoc.
- `IdsTest` (shared-kernel): wersja `7`, 1000 kolejnych ID rosnących i unikalnych, porządek tekstowy zgodny z kolejnością tworzenia (tak sortuje Postgres).
- `DietPlan.MAX_NAME_LENGTH = 200` + reguła w `create()`, zsynchronizowana z `VARCHAR(200)` w migracji. Dwa testy granicy: 200 znaków przechodzi, 201 jest odrzucane z czytelnym komunikatem.
- `flyway-database-postgresql` jako `runtimeOnly`.

**Weryfikacja** (tymczasowy Postgres, port 55432): `./gradlew build` ✅, **24 testy** (shared-kernel 7, module-diet 14, app 3).

**Eksperyment z kryteriów** (`kcal` → `calories` w encji, potem wycofany):
```
Schema validation: missing column [calories] in table [diet_plans]
```
Aplikacja nie startuje. To jest sens `validate`: rozjazd encji i schematu wychodzi przy starcie, a nie dopiero przy pierwszym zapytaniu na produkcji. Hibernate niczego nie „naprawia” sam, zmiana schematu = nowa migracja.

**Do zrobienia przez Ciebie:** `docker compose down -v` i `docker compose up -d` (lokalna baza ma tabelę z T-005 bez historii Flyway). Potem `./gradlew build` i `bootRun` działają na Twojej bazie.

Uwaga do limitu długości: Postgres liczy w `VARCHAR(200)` znaki (code points), a Kotlin `String.length` liczy jednostki UTF-16 (emoji = 2). Domena jest więc ostrzejsza albo równa bazie i nigdy nie przepuści czegoś, co baza odrzuci.

### Runda końcowa — 2026-10-11 → **DONE** ✅
- `./gradlew clean build` na czystym Postgresie: ✅ 24 testy, migracja `v2026.10.11.1200` zastosowana, `validate` OK.
- `bootRun`: POST `201` → GET z `Location` `200`; ID w bazie w wersji 7; `flyway_schema_history` z `success = t`.
- Nazwa z 201 znakami odrzucona przez regułę domeny (`Diet plan name must be at most 200 characters`), a nie przez bazę. Odpowiedź to na razie `500`, mapowanie na `400` to zakres E1.
- Brak `UUID.randomUUID()` w kodzie produkcyjnym, brak wildcard importów, komentarze z rundy 2 usunięte.
- Lokalna baza wymaga jeszcze `docker compose down -v` + `up -d` (stara tabela z T-005 bez historii Flyway).
