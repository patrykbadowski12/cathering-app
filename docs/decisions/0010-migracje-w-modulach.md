# 0010 — Migracje Flyway w modułach, z wersjami opartymi na dacie

**Status:** Accepted · **Data:** 2026-10-11 · **Doprecyzowuje:** 0001, 0003 · **Wdrożenie:** T-006

## Kontekst
Migracje mogą leżeć centralnie w `app` albo w module, który jest właścicielem tabel. Flyway skanuje `classpath:db/migration` we wszystkich jarach na classpath, więc działa w obu wariantach. Przy migracjach w wielu modułach numery `V1`, `V2`… zaczynają kolidować.

## Decyzja
- **Moduł jest właścicielem swoich tabel i ich migracji:** `module-xxx/src/main/resources/db/migration/`. `app` nie zawiera migracji tabel biznesowych.
- **Wersja = data i godzina:** `V<yyyy>_<MM>_<dd>_<HHmm>__<opis>.sql`, np. `V2026_10_11_1200__create_diet_plans.sql`. Unikalna globalnie bez koordynacji między modułami, a kolejność wynika z czasu powstania.
- Jedna wspólna tabela historii (`flyway_schema_history`) dla całej aplikacji. Osobne schematy lub historie per moduł rozważymy dopiero przy faktycznym wydzielaniu serwisu (ADR 0001).
- Zastosowanej migracji **nie edytujemy** (Flyway sprawdza sumy kontrolne). Zmiana schematu = nowa migracja.
- Bez `baseline-on-migrate`: na niepustej bazie bez historii Flyway ma się zatrzymać, a nie po cichu pominąć migracje.

## Konsekwencje
- Przy wydzielaniu modułu do serwisu jego migracje idą razem z nim.
- Dwie migracje z tą samą minutą w różnych modułach dałyby konflikt wersji. Flyway zatrzyma się wtedy z błędem, więc problem nie przejdzie niezauważony. W razie potrzeby dodajemy sekundy.
- Constrainty i indeksy nazywamy jawnie (`pk_`, `ck_`, `idx_`, `fk_` + tabela + opis).
