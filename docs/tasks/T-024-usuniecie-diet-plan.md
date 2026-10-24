# T-024 — Usunięcie starego modelu `DietPlan`

**Status:** TODO · **Etap:** A / E1 · **Kto:** Ty · **Zależy od:** T-022, T-023 · **ADR:** 0014

## Cel
Po przejściu frontu na `/diets` w kodzie i bazie nie zostaje nic ze starego modelu `DietPlan`. To ostatni krok zmiany modelu *expand → migrate → contract*.

## Czego się uczysz
- **Kontrakcja po migracji:** usuwamy dopiero wtedy, gdy nikt już nie korzysta ze starego API. Jak byś to sprawdził na produkcji (logi, metryki wywołań), gdyby korzystali z niego też inni klienci?
- **Migracja usuwająca dane:** `DROP TABLE` jest nieodwracalny. Kiedy warto najpierw zrobić kopię albo zostawić tabelę na jedno wydanie dłużej?

## Kryteria akceptacji
- [ ] Usunięte: `DietPlan` (domena, wyjątki, repozytorium), encja i repozytorium JPA, use case'y, kontrolery `/diet-plans`, DTO i testy.
- [ ] Nowa migracja usuwa tabelę `diet_plans` (stara migracja zostaje bez zmian).
- [ ] OpenAPI odświeżone: w kontrakcie nie ma `/diet-plans`. Front po `api:generate` dalej się kompiluje.
- [ ] Słownik: `Diet` bez dopisku o `DietPlan`.
- [ ] `./gradlew check` i testy frontu zielone.
