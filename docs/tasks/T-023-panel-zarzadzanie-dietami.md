# T-023 — Panel: zarządzanie dietami i wariantami

**Status:** TODO · **Etap:** A / E1 · **Kto:** Claude · **Zależy od:** T-020 · **ADR:** 0011, 0012, 0014

## Cel
Właściciel w `/admin/diety` tworzy dietę z wariantami, edytuje ją i włącza/wyłącza diety i warianty, a błędy z API widzi przy właściwych polach.

## Kryteria akceptacji
- [ ] Lista diet: nazwa, warianty (kcal + cena), status; nieaktywne wyraźnie oznaczone.
- [ ] Formularz nowej diety: nazwa, opis i pierwszy wariant (kcal + cena w złotych); po utworzeniu przejście do edycji, gdzie dodaje się kolejne warianty. Kwoty wpisywane w złotych z przecinkiem, wysyłane w formacie z kontraktu.
- [ ] Edycja diety: nazwa i opis, dodanie wariantu, zmiana ceny, włączenie/wyłączenie diety i wariantu z potwierdzeniem.
- [ ] Błędy reguł domeny (np. wyłączenie ostatniego aktywnego wariantu) pokazane przy właściwym miejscu.
- [ ] Obecny formularz dodawania `DietPlan` usunięty; panel nie korzysta już z `/diet-plans`.
- [ ] Testy komponentów: tworzenie z wariantami, walidacja, wyłączanie, błąd z API.
