# T-016 — Jadłospis tygodniowy w sklepie (dane przykładowe)

**Status:** DONE · **Etap:** 0 · **Kto:** Claude · **ADR:** 0011, 0012

## Cel
Klient widzi na stronie jadłospis na bieżący tydzień. Dane są na razie przykładowe, ale w kształcie przyszłego API z E2 (dieta → dzień → posiłki), więc podmiana na prawdziwy endpoint dotyczy jednej funkcji.

## Kryteria akceptacji
- [x] Sekcja „Jadłospis na ten tydzień” (`#jadlospis`) z zakładkami pon–nd i datami bieżącego tygodnia; dzisiejszy dzień zaznaczony domyślnie.
- [x] Dla wybranego dnia: 5 posiłków (śniadanie, II śniadanie, obiad, podwieczorek, kolacja) z nazwą, kcal, oznaczeniem wegetariańskim i sumą kcal dnia.
- [x] Dostępność: zakładki z `role="tab"`/`tabpanel`, obsługa strzałek, Home i End.
- [x] Dane w `features/menu/menuApi.ts` (`fetchWeeklyMenu`, `TODO(E2)`), pobierane przez TanStack Query jak diety.
- [x] Link „Jadłospis” w nawigacji.
- [x] Wybór diety nad jadłospisem (diety z API); wybór w adresie `?dieta=<id>`, więc da się go udostępnić. Przy zmianie diety wybrany dzień zostaje.
- [x] Na karcie diety przycisk „Zobacz jadłospis”, który przewija do jadłospisu z tą dietą (także przy otwarciu linku od zera, po załadowaniu diet).
- [x] Dane przykładowe zależne od diety: porcje przeskalowane do kcal diety, zestaw dań różny między dietami.
- [x] Testy: 7 dni z zaznaczonym dzisiejszym, posiłki wybranego dnia, przełączanie kliknięciem i klawiaturą; domyślna dieta, zmiana diety (suma kcal ≈ kcal diety), dieta z linku, link na karcie.

## Do zrobienia w E2
- Endpoint jadłospisu (np. `GET /api/v1/shop/diet-plans/{id}/menu?from=&to=`) i podmiana `fetchWeeklyMenu` na wygenerowanego klienta.
