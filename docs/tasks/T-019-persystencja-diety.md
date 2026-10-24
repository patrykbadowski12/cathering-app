# T-019 — Persystencja agregatu `Diet` (migracja + JPA)

**Status:** TODO · **Etap:** A / E1 · **Kto:** Ty · **Zależy od:** T-018 · **ADR:** 0003, 0010, 0014

## Cel
Agregat `Diet` z wariantami zapisuje się w PostgreSQL i wraca w całości. Baza sama pilnuje tego, co da się wyrazić ograniczeniami.

## Czego się uczysz
- **Zmiana modelu bez przestoju (*expand → migrate → contract*):** nowe tabele powstają obok `diet_plans`, front przechodzi na nowe API (T-022, T-023), a stara tabela znika dopiero w T-024. Nie edytujemy zastosowanej migracji, tylko dodajemy nowe. Zastanów się, co byś zrobił, gdyby w `diet_plans` były już dane produkcyjne.
- **Mapowanie agregatu z kolekcją w JPA:** `@OneToMany` z kaskadą i `orphanRemoval` vs osobne repozytorium wariantów. Problem N+1 przy liście diet i jak go uniknąć (`JOIN FETCH`, `@EntityGraph`, `@BatchSize`).
- **Ograniczenia w bazie jako druga linia obrony:** unikalność `(tenant_id, slug)` i `(diet_id, kcal)`, `CHECK` dla cen i kaloryczności.
- **Encja JPA ≠ model domeny:** mapowanie w obie strony (jak `DietPlanEntity`), szczególnie przy kolekcji.

## Kryteria akceptacji
- [ ] Nowa migracja w `module-diet` (konwencja nazw z ADR 0010) tworzy `diets` i `diet_variants` (bez ruszania `diet_plans`) z kluczami, indeksami i ograniczeniami (unikalny slug per tenant, unikalna kaloryczność per dieta, ceny i kcal > 0, poprawne stany).
- [ ] Kolumny audytu w obu tabelach (jak w `diet_plans`).
- [ ] Port `DietRepository` w domenie i implementacja w `infrastructure`: zapis, odczyt po id i tenancie, odczyt po slugu i tenancie, lista diet tenanta, sprawdzenie zajętości sluga.
- [ ] Zapis zmienionego agregatu (np. dodany wariant, zmieniona cena) aktualizuje wiersze, a nie duplikuje ich.
- [ ] Testy z Testcontainers (jak T-007):
  - zapis i odczyt diety z wariantami;
  - izolacja tenantów;
  - naruszenie unikalności sluga lub kaloryczności kończy się błędem bazy;
  - lista diet nie robi zapytania na każdą dietę (licznik zapytań Hibernate, jak w `DietPlanRepositoryImplTest`).

## Wskazówki
- `ddl-auto: validate` złapie każdą rozbieżność między encją a migracją przy starcie testów. To Twój najszybszy sygnał.
- Przy `@OneToMany` zwróć uwagę, która strona jest właścicielem relacji i czy potrzebujesz relacji dwukierunkowej.
- Kotlin + JPA: kolekcja w encji musi być mutowalna (`MutableList`), nawet jeśli domena wystawia tylko `List`.
