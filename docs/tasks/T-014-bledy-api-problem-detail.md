# T-014 — Błędy API jako `ProblemDetail`

**Status:** TODO · **Etap:** 0 · **Kto:** Ty · **ADR:** 0011

## Cel
Błędy walidacji i „nie znaleziono” zwracają spójną odpowiedź `application/problem+json` (RFC 9457) z czytelnym kodem HTTP, a nie `500` ze stack trace'em. Formularz na froncie wie, które pole jest źle wypełnione.

## Czego się uczysz
- `@RestControllerAdvice` i `ProblemDetail` w Springu (`spring.mvc.problemdetails.enabled`).
- Granica między wyjątkami domeny a protokołem HTTP: domena rzuca swoje wyjątki i nie wie nic o kodach HTTP, a mapowanie robi warstwa `web`.
- Dwie warstwy walidacji: **kształtu requestu** (np. brak pola, zły typ, czyli Bean Validation na DTO) vs **reguł biznesowych** (domena).

## Kryteria akceptacji
- [ ] POST z pustą nazwą / `kcal <= 0` / nazwą > 200 znaków → `400` z `ProblemDetail` i listą błędów per pole (np. `errors: [{ field: "name", message: "…" }]`).
- [ ] Niepoprawny JSON albo brak wymaganego pola → `400` (nie `500`).
- [ ] Nieznane id → `404` jako `ProblemDetail`.
- [ ] Nieoczekiwany błąd → `500` bez szczegółów technicznych w odpowiedzi (stack trace tylko w logu, z tenantem z MDC).
- [ ] Domena nie importuje niczego ze Springa (ADR 0005). Rozważ własne wyjątki domenowe zamiast gołego `IllegalArgumentException`.
- [ ] Kształt błędu opisany w OpenAPI (jeśli T-013 jest już zrobione).
- [ ] Testy, np. w ramach T-011 (`@WebMvcTest`).

## Review
_(uzupełnia Claude)_
