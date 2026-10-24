# 0014 — Model diety: warianty kaloryczne z ceną

**Status:** Accepted · **Data:** 2026-10-18 · **Wdrożenie:** E1

## Kontekst
Dziś `DietPlan` to para „nazwa + kaloryczność”. Keto 1500 i Keto 2000 to dwa niezależne plany. Ceny jeszcze nie ma.

Na polskim rynku cateringów dietetycznych klient wybiera **dietę** (Standard, Keto, Wege…), a potem **kaloryczność**. Cena zależy zwykle od kaloryczności i jest podawana za dzień. Jadłospis jest wspólny dla diety, a kaloryczności różnią się wielkością porcji.

Od tego modelu zależą jadłospis (E2), zapytania (E3), zamówienia i lista produkcyjna. Zmiana po E2 oznaczałaby migrację danych w kilku modułach.

Rozważane opcje:
- **A. Płasko (stan obecny):** jedna dieta = jedna kaloryczność. Najprostsze, ale jadłospis trzeba by wpisywać osobno dla każdej kaloryczności, a sklep pokazuje kilka kart tej samej diety.
- **B. Dieta z wariantami kalorycznymi:** wybrana.
- **C. Cena liczona z kaloryczności** (stawka bazowa + stawka za kcal, dowolne kcal): nietypowe na rynku, a kuchnia i lista produkcyjna potrzebują skończonego zbioru porcji.

## Decyzja
- **Dieta (`Diet`)** należy do tenanta: nazwa, opis, slug (do URL-i, ADR 0008), stan (aktywna/nieaktywna). Ma co najmniej jeden **wariant**.
- **Wariant (`DietVariant`)** należy do diety: kaloryczność, cena za dzień, stan. Kaloryczność jest unikalna w obrębie diety. Listę kaloryczności definiuje właściciel; platforma nie narzuca stałego zestawu.
- **Cena per wariant**, za dzień, **brutto**. Ta sama cena we wszystkich wariantach też jest dozwolona. Rabatów (np. za dłuższy okres) w Fazie 1 nie ma.
- **`Money`:** kwota w groszach (`Long`), tylko PLN. Pole waluty dodamy, gdy pojawi się druga waluta.
- **Jadłospis należy do diety**, a nie do wariantu: wszystkie kaloryczności mają te same posiłki, różnią się porcjami.
- **Bez usuwania:** diety i warianty tylko dezaktywujemy, bo zapytania i zamówienia się do nich odwołują. Aktywna dieta musi mieć co najmniej jeden aktywny wariant.
- **Płatności poza systemem:** cena jest informacją dla klienta. Płatność odbywa się przelewem po ustaleniach z właścicielem (szczegóły w decyzji o zapytaniach i zamówieniach).

## Konsekwencje
- `Diet` to agregat z kolekcją wariantów i niezmiennikami (unikalna kaloryczność, co najmniej jeden aktywny wariant w aktywnej diecie, cena > 0).
- Nowe tabele (`diets` + `diet_variants`) powstają obok `diet_plans`. Stary model usuwamy na końcu E1, gdy front przejdzie na nowe API. Danych produkcyjnych jeszcze nie ma, więc nie trzeba ich przenosić.
- Zmiana nazwy `DietPlan` → `Diet` w kodzie, API i słowniku; nowe endpointy E1 w `/api/v1/shop` i `/api/v1/admin`.
- Sklep: jedna karta na dietę z ceną „od X zł/dzień” i wyborem kaloryczności. Panel: edycja diety razem z wariantami.
- Zapytania i zamówienia wskazują **wariant** (a przez niego dietę).
