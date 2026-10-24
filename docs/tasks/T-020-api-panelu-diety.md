# T-020 — API panelu: zarządzanie dietami i wariantami

**Status:** TODO · **Etap:** A / E1 · **Kto:** Ty · **Zależy od:** T-019 · **ADR:** 0009, 0012, 0014

## Cel
Właściciel przez `/api/v1/admin/diets` tworzy diety z wariantami, edytuje je i włącza/wyłącza. Kontrakt jest opisany w OpenAPI, więc front (T-023) generuje z niego klienta.

## Czego się uczysz
- **Projektowanie REST dla agregatu:** zasób zagnieżdżony (`/diets/{id}/variants`) vs płaski; zmiana stanu jako `PATCH` pola vs osobna akcja (`POST /{id}/deactivate`). Zdecyduj i uzasadnij przy review.
- **Use case'y jako granica transakcji** (ADR 0009): jeden use case = jedna operacja właściciela.
- **Reguła wymagająca repozytorium:** unikalność sluga w obrębie tenanta sprawdza warstwa `application`, bo domena nie widzi innych diet. Co, jeśli dwa requesty wpadną naraz? Tu wraca ograniczenie w bazie z T-019.
- **Reprezentacja pieniędzy w JSON-ie:** grosze jako liczba całkowita czy kwota dziesiętna jako tekst. Front tylko formatuje to, co dostanie.

## Kryteria akceptacji
- [ ] Endpointy panelu:
  - lista wszystkich diet tenanta (także nieaktywnych) z wariantami;
  - szczegóły diety;
  - utworzenie diety z dokładnie jednym, pierwszym wariantem (`201` + `Location`);
  - zmiana nazwy i opisu;
  - dodanie kolejnego wariantu (osobny endpoint) i zmiana ceny wariantu;
  - aktywacja/dezaktywacja diety i wariantu.
- [ ] Slug zajęty w obrębie tenanta → zamiast błędu dostaje przyrostek (`keto`, `keto-2`), albo `409`/`400` z polem. Wybierz zachowanie i opisz je w OpenAPI.
- [ ] Błędy jako `ProblemDetail` z polami (T-014): walidacja requestu i reguły domeny z T-018 (np. ostatni aktywny wariant).
- [ ] `404` dla diety innego tenanta.
- [ ] OpenAPI odświeżone (`openapi.json`), odpowiedzi błędów opisane.
- [ ] Stare `/api/v1/admin/diet-plans` dalej działa (front przechodzi na nowe API w T-023, a stare usuwamy w T-024).
- [ ] Testy: slice `@WebMvcTest` (wzór z T-011) i jeden test integracyjny ścieżki „utwórz → dezaktywuj wariant → odczytaj”.

## Wskazówki
- Najpierw rozpisz sobie kontrakt (ścieżki, requesty, odpowiedzi), zanim napiszesz kontroler. Daj mi go do przeglądu, bo front zależy od jego kształtu.
- Tworzenie przyjmuje jeden wariant (decyzja z 2026-10-20): `Diet.create` dostaje pierwszy wariant, kolejne idą przez `addVariant`, więc unikalność kcal sprawdza tylko `addVariant`. Zagnieżdżony obiekt wariantu w requeście wymaga `@Valid`, żeby jego pola były walidowane.
- Po zmianie API: `-Dopenapi.update=true`, a potem daj znać, żebym wygenerował klienta na froncie.
