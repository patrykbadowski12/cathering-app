# T-022 — Sklep: karty diet z ceną i strona szczegółów diety

**Status:** TODO · **Etap:** A / E1 · **Kto:** Claude · **Zależy od:** T-021 · **ADR:** 0011, 0012, 0014

## Cel
Klient widzi jedną kartę na dietę z ceną „od X zł/dzień” i wchodzi w szczegóły diety, gdzie wybiera kaloryczność i widzi cenę wybranego wariantu.

## Kryteria akceptacji
- [ ] Klient API wygenerowany z nowego kontraktu; sklep nie korzysta już z `/diet-plans`.
- [ ] Karta diety: nazwa, krótki opis, zakres kaloryczności, „od X zł/dzień” (cena brutto, format polski: `59,90 zł`), link do szczegółów.
- [ ] Strona `/diety/:slug`: opis, wybór kaloryczności, cena wybranego wariantu za dzień, link do jadłospisu diety. Wybrany wariant w adresie, żeby dało się go udostępnić.
- [ ] Nieistniejąca lub wyłączona dieta → czytelna strona „nie znaleziono”.
- [ ] Jadłospis (T-016) wybiera dietę po slugu zamiast po id.
- [ ] Przycisk zapytania na stronie diety przygotowany pod E3 (na razie nieaktywny).
- [ ] Testy komponentów: lista, szczegóły, wybór wariantu, `404`.
