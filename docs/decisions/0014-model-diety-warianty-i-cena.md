# 0014 — Model diety: warianty kaloryczne i cena

**Status:** Proposed · **Data:** 2026-10-18 · **Rozstrzygamy:** przed rozpisaniem E1

## Kontekst
Dziś `DietPlan` to para „nazwa + kaloryczność”. Keto 1500 i Keto 2000 to dwa niezależne plany. Ceny jeszcze nie ma (E1: „cena jako `Money`”).

Na polskim rynku cateringów dietetycznych typowy układ wygląda tak:
- klient wybiera **dietę** (Standard, Keto, Wege…), a potem **kaloryczność** z kilku stałych wariantów (np. 1200, 1500, 2000, 2500, 3000);
- **cena zależy od wariantu** i jest podawana za dzień;
- jadłospis jest wspólny dla diety, a warianty różnią się wielkością porcji.

Od tej decyzji zależą schemat bazy (E1), jadłospis (E2), zamówienie (E3) i lista produkcyjna (E5). Zmiana po E2 oznacza migrację danych w kilku modułach.

## Rozważane opcje

### A. Płasko: jedna dieta = jedna kaloryczność (stan obecny)
`DietPlan(name, kcal, price)`.
- **Za:** najprostsze, już działa; zamówienie wskazuje jeden obiekt.
- **Przeciw:**
  - jadłospis trzeba wpisywać osobno dla każdej kaloryczności (5× ta sama praca dla właściciela) albo i tak powiązać plany „grupą”;
  - sklep pokazuje 5 kart „Keto”;
  - nie ma pojęcia „ta sama dieta, inna kaloryczność”, potrzebnego np. do zmiany kaloryczności w trakcie zamówienia.

### B. Dieta z wariantami (rekomendacja)
`Diet(name, description, slug, state)` 1 → n `DietVariant(kcal, pricePerDay, state)`.
- **Za:**
  - odpowiada temu, jak myśli właściciel i klient;
  - jadłospis (E2) przypięty do diety, a nie do wariantu;
  - zamówienie (E3) wskazuje wariant;
  - sklep pokazuje jedną kartę z ceną „od X zł/dzień” i wyborem kaloryczności.
- **Przeciw:**
  - agregat z kolekcją (reguły: unikalne `kcal` w diecie, co najmniej jeden aktywny wariant w aktywnej diecie);
  - przebudowa obecnego `DietPlan`, API i frontu.

### C. Dieta z ceną liczoną z kaloryczności
`Diet(name, basePrice, pricePerKcal, minKcal, maxKcal)`, klient wpisuje dowolne kcal.
- **Za:** elastyczne, jeden rekord.
- **Przeciw:**
  - nietypowe na rynku i trudne do wyjaśnienia właścicielowi;
  - kuchnia nie przygotuje „1837 kcal”;
  - lista produkcyjna potrzebuje skończonego zbioru porcji.

## Pytania do rozstrzygnięcia razem z wyborem
- **Jadłospis:** wspólny dla diety ze skalowanymi porcjami czy osobny per wariant? Wspólny pasuje do B i do danych przykładowych z T-016.
- **Cena:** za dzień, zawsze brutto (klient końcowy jest konsumentem). Rabaty za dłuższy okres: w Fazie 1 czy później?
- **Reprezentacja `Money`:** kwota w groszach (`Long`) czy `BigDecimal` + waluta? Czy waluta jest w ogóle potrzebna, skoro działamy tylko w PLN?
- **Warianty kaloryczne:** stała lista platformy czy dowolna lista per tenant?
- **Usuwanie:** wariant z zamówieniami tylko dezaktywujemy, nigdy nie usuwamy?

## Decyzja
_Do uzupełnienia po rozmowie._

## Konsekwencje
_Do uzupełnienia po decyzji._ Przy wyborze B: nowa migracja (`diets` + `diet_variants`), zmiana nazwy `DietPlan` → `Diet` w kodzie i słowniku, nowe endpointy E1, przebudowa karty diety w sklepie i formularza w panelu.
