# T-018 — Agregat `Diet` z wariantami i `Money` (domena)

**Status:** DONE · **Etap:** A / E1 · **Kto:** Ty · **ADR:** 0005, 0014

## Cel
Nowy model domeny z ADR 0014 istnieje w czystym Kotlinie i pilnuje swoich reguł: dieta z wariantami kalorycznymi, cena jako `Money`, aktywacja i dezaktywacja. Na razie bez bazy i API. Obecny `DietPlan` zostaje nietknięty: stary i nowy model żyją obok siebie do końca E1, a usuwamy stary w T-024.

## Czego się uczysz
- **Agregat z kolekcją:** korzeń (`Diet`) jest jedynym wejściem do zmian wariantów. Nikt z zewnątrz nie zmienia wariantu z pominięciem diety, więc reguły obejmujące całą kolekcję (unikalność kaloryczności, „aktywna dieta ma aktywny wariant”) da się sprawdzić w jednym miejscu.
- **Value object:** `Money` porównywany po wartości, niezmienny, z własnymi regułami (np. bez ujemnych kwot). Gdzie kończy się value object, a zaczyna encja (wariant ma tożsamość, cena nie).
- **Niemutowalność vs mutowalność agregatu w Kotlinie:** metody zwracające nową kopię (`copy`) czy zmieniające stan. Oba podejścia są do obrony. Wybierz świadomie, bo wpływa to na mapowanie w T-019.
- **Typed ID:** `DietId`, `DietVariantId` zamiast gołych `UUID` (jak `DietPlanId`).

## Kryteria akceptacji
- [x] `Money` w `shared-kernel` albo w `diet.domain` (zdecyduj i uzasadnij przy review): kwota w groszach (`Long`), PLN, bez ujemnych wartości, czytelne tworzenie (np. z groszy).
- [x] `Diet`: tenant, nazwa (niepusta, max 200 znaków), wymagany opis (z limitem długości; decyzja 2026-10-24: opis potrzebny na karcie w sklepie), slug, stan, lista wariantów.
- [x] `DietVariant`: kaloryczność (> 0), cena za dzień (> 0), stan.
- [x] Tworzenie diety wymaga co najmniej jednego wariantu. Nowa dieta i nowe warianty są aktywne.
- [x] Operacje na diecie: zmiana nazwy i opisu, dodanie wariantu, zmiana ceny wariantu, aktywacja/dezaktywacja diety i wariantu.
- [x] Reguły:
  - kaloryczność unikalna w obrębie diety (także wśród nieaktywnych wariantów);
  - nie da się dezaktywować ostatniego aktywnego wariantu aktywnej diety;
  - nie da się aktywować diety bez aktywnego wariantu.
- [x] Naruszenie reguły rzuca wyjątek domenowy z polem (`DomainValidationException` z T-014), żeby API mogło pokazać błąd przy właściwym polu.
- [x] Slug: generowany z nazwy, małe litery, `[a-z0-9-]`, polskie znaki zamienione (`Wegańska` → `weganska`). Unikalność w obrębie tenanta to sprawa T-020, bo domena nie widzi innych diet.
- [x] Testy jednostkowe wszystkich reguł (bez Springa, jak w T-003).
- [x] `ArchitectureTest` przechodzi: domena dalej bez Springa i JPA.

## Wskazówki
- Zacznij od testów reguł z listy wyżej. Dobrze pokazują, jakie API agregatu jest wygodne.
- Zastanów się, czy lista wariantów na zewnątrz ma być `List` (tylko do odczytu) i jak schować możliwość jej zmiany.
- Czy zmiana ceny wariantu, który był użyty w zamówieniu, to problem? Na razie nie (zamówień jeszcze nie ma), ale zapisz sobie to pytanie do E5: zamówienie powinno pamiętać cenę z chwili zamówienia.
- Generowanie sluga to czysta funkcja: łatwa do przetestowania parametryzowanym testem.
