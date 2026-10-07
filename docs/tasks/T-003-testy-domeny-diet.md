# T-003 — Testy jednostkowe domeny `DietPlan`

**Status:** DONE · **Etap:** 0 · **Zależy od:** T-002

## Cel
Reguły biznesowe `DietPlan` i use case'ów są pokryte szybkimi testami jednostkowymi, bez kontekstu Springa i bez bazy.

## Czego się uczysz
- Testowanie domeny w izolacji: to jest główna korzyść z ADR 0005.
- **Fake vs mock:** prosta implementacja `DietPlanRepository` w pamięci zamiast biblioteki do mocków. Kiedy które ma sens?
- Struktura testu: given / when / then, nazwy testów opisujące zachowanie.

## Kryteria akceptacji
- [ ] Testy w `module-diet/src/test/kotlin/...`, w pakietach odpowiadających kodowi (`domain`, `application`). Zależności (`kotlin-test-junit5`) są już dodane w głównym `build.gradle.kts` dla wszystkich modułów.
- [ ] `DietPlan.create()`: pusta nazwa (także same spacje) jest odrzucana, `kcal <= 0` jest odrzucane (sprawdź wartości graniczne), a poprawne dane dają plan w stanie `ACTIVE` z przekazanym `tenantId`.
- [ ] Use case'y przetestowane z **fake'owym** repozytorium (w pamięci, w `src/test`):
  - `CreateDietPlanUseCase`: zapisuje plan; przy niepoprawnych danych **nic** nie trafia do repozytorium.
  - `ListDietPlanUseCase`: zwraca tylko plany danego tenanta.
  - `GetDietPlanUseCase`: zwraca własny plan; dla planu **innego tenanta** i dla nieistniejącego id zwraca `null`. To test bezpieczeństwa.
- [ ] Żaden test nie startuje Springa (`@SpringBootTest` jest zakazany w tym zadaniu).
- [ ] `./gradlew :module-diet:test` przechodzi i trwa sekundy, nie dziesiątki sekund.
- [ ] Sanity check: celowo zepsuj jedną regułę w `DietPlan` (np. `kcal >= 0`), zobacz, że test czerwienieje, a potem przywróć.

## Wskazówki
- Nazwy testów w Kotlinie mogą być zdaniami w backtickach, np. ``fun `should reject blank name`()``.
- Do asercji wystarczy `kotlin.test`. Możesz też spróbować AssertJ albo Kotest assertions i ocenić, co czyta się lepiej.

## Review

### Runda 1 — 2026-10-05 · gałąź `task/T-003-tests` → **CHANGES**

**Jak sprawdzone:**
- `./gradlew :app:test`: 8 testów zielonych, każdy trwa milisekundy ✅.
- Na kopii projektu przeniosłem testy do `module-diet` i zrobiłem **testy mutacyjne**: celowo psułem kod i sprawdzałem, czy któryś test to wyłapie.

| Mutacja | Czy test złapał? |
|---|---|
| `require(kcal > 0)` → `require(kcal >= 0)` | ✅ `should throw exception when kcal is zero` |
| Fake: `findByIdAndTenantId` ignoruje `tenantId` | ❌ **wszystko zielone** |
| `CreateDietPlanUseCase` zapisuje plan dwa razy | ❌ **wszystko zielone** |

**Do poprawy (blokujące):**
1. **Testy są w `app/src/test`, a nie w `module-diet/src/test`.** Testy modułu należą do modułu:
   - `./gradlew :module-diet:test` teraz nie uruchamia **niczego**,
   - po wydzieleniu modułu (ADR 0001) testy zostałyby w `app`,
   - `module-diet` ma już wszystkie potrzebne zależności. Sprawdziłem: po przeniesieniu wszystko przechodzi bez zmian w buildzie.
2. **Brak testu bezpieczeństwa w `GetDietPlanUseCase`** (mutacja 2 przeżyła). Brakuje dwóch testów:
   - plan tenanta A, pytanie jako tenant B → `null`,
   - nieistniejące id → `null`.

   To najważniejszy test w tym zadaniu, bo pilnuje izolacji danych między cateringami.
3. **Test `Create` sprawdza tylko wartość zwróconą, a nie stan repozytorium** (mutacja 3 przeżyła). Use case mógłby niczego nie zapisać albo zapisać dwa razy, a test i tak by przeszedł, bo `result` pochodzi z `DietPlan.create()`. Sprawdź, co faktycznie jest w repozytorium, np. przez `findAllByTenantId`. Brakuje też testu „niepoprawne dane → **nic** nie zostało zapisane”.
4. **Fake zachowuje się inaczej niż prawdziwe repozytorium.** `ArrayList` + `add` przy dwukrotnym `save()` tego samego planu tworzy **duplikat**. Baza z kluczem głównym zachowałaby się inaczej: jeden wiersz, czyli upsert. Fake musi trzymać się kontraktu, inaczej testy kłamią. Użyj mapy z kluczem `DietPlanId`.

**Do poprawy (drobne):**
- **Trzy biblioteki asercji naraz:** `org.junit.jupiter.api.assertNotNull/assertThrows`, `kotlin.test.assertEquals` i `Assertions.*` (import z gwiazdką). Wybierz jedną. Proponuję `kotlin.test`, która ma `assertFailsWith`, `assertNotNull`, `assertEquals` i `assertNull`.
- **`assertNotNull(result.id)` niczego nie sprawdza**, bo `id` ma typ non-null i kompilator już to gwarantuje. Lepiej sprawdzić, że dwa `create()` dają różne ID.
- **Brak granicy po stronie akceptacji:** `kcal = 1` powinno przejść. Bez tego mutacja `kcal > 1` też by przeżyła.
- **Test listy sprawdza tylko `size == 2`.** Przeszedłby też, gdyby wróciły dwa *złe* plany. Sprawdź, że wszystkie wyniki mają właściwy `tenantId` albo że to te konkretne plany.
- `companion object { val TEST_NAME … }` → `const val` (albo `private const val` na poziomie pliku).
- Nazwa `name is fake empty` → `name is blank`, bo tak brzmi termin w Kotlinie (`isBlank`).
- Dane testowe: `kcal = 10` przy diecie jest nierealne. Realistyczne dane (`"Keto", 1500`) sprawiają, że test czyta się jak przykład z biznesu.

**Co jest dobre:**
- Wyraźne `given/when/then`.
- Nazwy testów są zdaniami.
- Sprawdzasz komunikat wyjątku, nie tylko jego typ.
- Przypadek „same spacje” jest pokryty.
- Brak Springa w testach, a całość trwa milisekundy.

**Pytanie:** czy zrobiłeś sanity check z kryteriów (zepsuć regułę → czerwony test)? Tabela wyżej to w praktyce to samo, tylko zrobione systematycznie. Ta technika nazywa się *mutation testing* i istnieją narzędzia, które robią to automatycznie, np. PIT.

### Runda 2 — 2026-10-05 → **CHANGES**
- ✅ Punkt 1: testy przeniesione do `module-diet`, a `./gradlew :module-diet:test` uruchamia 8 testów (zielone, ~2 s).
- ⚠️ `DietPlanTest.kt` leży w katalogu `.../diet/`, a ma `package ...diet.domain`. Kotlin to kompiluje, ale konwencja (i ostrzeżenie IntelliJ) wymaga, żeby katalog odpowiadał pakietowi. Przenieś do `.../diet/domain/`.
- ⏳ Punkty 2–4 oraz drobne z rundy 1 bez zmian. Treść testów i fake'a jest identyczna jak w rundzie 1, więc mutacje 2 i 3 nadal przeżywają.

### Runda 3 — 2026-10-07 → **CHANGES**
**Wynik:** `./gradlew :module-diet:test` ❌ `ListDietPlanUseCaseTest > should get 2 elements for requested tenant` (6 zamiast 2).

**Przyczyna (błąd w fake'u, nie w teście):** `findAllByTenantId` robi `filter { … }.flatMap { dietCollection.values }`. `flatMap` dla każdego z 2 przefiltrowanych wpisów dokleja **wszystkie** wartości mapy, czyli 2 × 3 = 6. Poprawnie: `dietCollection.values.filter { it.tenantId == tenantId }`. Ten sam błąd był niewidoczny w teście `Create` (1 × 1 = 1). To dobry przykład, po co testy z danymi wielu tenantów.

**Postęp:**
- ✅ Fake na mapie, więc `save()` działa jak upsert.
- ✅ Test „inny tenant → `null`” (mutacja 2 z rundy 1 byłaby teraz wyłapana).
- ✅ `Create` sprawdza stan repozytorium.
- ✅ Usunięty nieużywany import `DietPlan` w kontrolerze.

**Zostało (blokujące):**
1. Naprawić `findAllByTenantId` w fake'u.
2. `GetDietPlanUseCaseTest`: nieistniejące id → `null`.
3. `CreateDietPlanUseCaseTest`: pusta nazwa → wyjątek **i** repozytorium puste.
4. `DietPlanTest.kt` → katalog `.../diet/domain/`.

**Drobne (z rundy 1, nadal aktualne):**
- Jedna biblioteka asercji zamiast trzech.
- Usunąć `assertNotNull(result.id)`.
- Dodać granicę `kcal = 1`.
- Test listy ma sprawdzać, *czyje* plany wróciły, a nie tylko ich liczbę.
- `findByIdAndTenantId` w fake'u można skrócić do `dietCollection[id]?.takeIf { it.tenantId == tenantId }`.

### Runda 4 — 2026-10-07 → **CHANGES** (ostatnia prosta)

**Testy mutacyjne** (na kopii, tylko testy deterministyczne):

| Mutacja | Wynik |
|---|---|
| `kcal > 0` → `kcal > 1` | ✅ złapana (test `1 kcal`) |
| `isNotBlank()` → `isNotEmpty()` | ✅ złapana (test „same spacje”) |
| Fake ignoruje tenanta w `findByIdAndTenantId` | ✅ złapana (test „inny tenant”) |
| `CreateDietPlanUseCase` nie zapisuje | ✅ złapana (sprawdzanie stanu repo) |
| `DietPlan.create()` ignoruje `tenantId` | ✅ złapana |

Wszystkie mutacje są złapane. Testy faktycznie pilnują reguł.

**Blokujące:**
1. **`ListDietPlanUseCaseTest` jest niestabilny (flaky).** Na 10 uruchomień przeszedł 4 razy, a padł 6 razy. Test sprawdza `result[0]` i `result[1]`, czyli **kolejność**, a `HashMap` nie gwarantuje kolejności iteracji. Klucze to losowe UUID, więc kolejność jest losowa przy każdym uruchomieniu. Kontrakt `findAllByTenantId` też nie obiecuje żadnej kolejności, a SQL bez `ORDER BY` również jej nie daje. Asercja ma być niezależna od kolejności: porównaj **zbiory** (np. nazw albo ID), a osobno sprawdź, że wszystkie wyniki mają właściwy `tenantId`.
2. **`DietPlanTest.kt`** nadal leży w `.../diet/`, a ma `package ...diet.domain`. Przenieś do `.../diet/domain/`.

**Drobne (nie blokują):**
- `CreateDietPlanUseCaseTest`: `assertNotNull(result.id)` nadal niczego nie sprawdza.
- `companion object { val … }` → `const val`.
- Asercje są już prawie wszędzie z JUnit (`Assertions`) ✅. Ta spójność wystarczy.

### Runda 5 — 2026-10-07 → **DONE** ✅

**Jak sprawdzone:**
- `./gradlew :module-diet:test` 10 razy z rzędu: **10/10 zielonych**, niestabilność usunięta.
- Pełny `./gradlew build` ✅.
- 12 testów, wszystkie w milisekundach, bez Springa.
- Mutacje z rundy 4 nadal są łapane (logika testów domeny i use case'ów się nie zmieniła).

**Co poprawione:**
- ✅ Test listy nie zależy od kolejności: sprawdza `size` i `tenantId` każdego wyniku.
- ✅ `DietPlanTest` w `.../diet/domain/`.
- ✅ `const val`.
- ✅ Usunięty pusty `assertNotNull(result.id)`.

**Do posprzątania przy commicie (nie blokuje):**
- `CreateDietPlanUseCaseTest`: nieużywany import `org.junit.jupiter.api.assertNotNull`.
- Opcjonalnie: test listy przeszedłby, gdyby use case zwrócił **dwa razy ten sam** plan (rozmiar 2 i właściwy tenant się zgadzają). Porównanie zbioru ID (`result.map { it.id }.toSet()` z oczekiwanym) domyka i tę lukę.

**Czego się nauczyłeś w tym zadaniu:**
- Fake a mock, i to, że fake musi trzymać się kontraktu (mapa zamiast listy, filtr po tenancie).
- Test stanu zamiast testu wartości zwróconej.
- Wartości graniczne.
- Testy mutacyjne jako sprawdzian jakości testów.
- `flatMap` a `filter`.
- Niestabilne testy i asercje niezależne od kolejności (kontrakt zamiast przypadkowego zachowania implementacji).
- Testy modułu należą do modułu.
