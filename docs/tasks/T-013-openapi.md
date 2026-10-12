# T-013 — Kontrakt API: OpenAPI generowane przez backend

**Status:** TODO · **Etap:** 0 · **Kto:** Ty · **ADR:** 0011

## Cel
Backend publikuje opis swojego API w formacie OpenAPI. To źródło prawdy dla frontu, z którego w T-015 generujemy typowanego klienta TypeScript.

## Czego się uczysz
- *Contract-first* vs *code-first*: tu code-first, czyli kontrakt wynika z kontrolerów i DTO, ale jest publikowany i wersjonowany.
- Jak typy Kotlina (nullability, value classes, enumy) przekładają się na schemat OpenAPI i dlaczego ma to znaczenie dla frontu (pole `string` vs `string | null`).
- Swagger UI jako narzędzie developerskie.

## Kryteria akceptacji
- [ ] `GET /v3/api-docs` zwraca specyfikację OpenAPI z endpointami `diet-plans`.
- [ ] Swagger UI dostępny lokalnie. Zdecyduj świadomie, czy ma być dostępny na produkcji (profil).
- [ ] Specyfikacja jest poprawna dla frontu:
  - pola nienullowalne w Kotlinie są w schemacie `required`,
  - `state` ma wartości enuma,
  - `id` jest typu `string` w formacie `uuid`.
- [ ] Odpowiedzi opisane z kodami (`201` z nagłówkiem `Location`, `404`, `400`).
- [ ] Wygenerowana specyfikacja zapisana w repo (np. `docs/api/openapi.json`) albo generowana w buildzie. Wybierz i uzasadnij.

## Wskazówki
- Najpopularniejsza biblioteka to **springdoc-openapi**. Sprawdź, która linia wersji obsługuje **Spring Boot 4** (starsze 2.x są dla Boot 3).
- Nullability: z `-Xjsr305=strict` i modułem Kotlina springdoc zwykle poprawnie wykrywa `required`. Sprawdź w wygenerowanym JSON-ie, a nie zakładaj.
- Endpoint `/v3/api-docs` przechodzi przez `TenantFilter`. Zastanów się, czy to problem (w Fazie 1 nie, w Fazie 2 z hostem tenanta tak).

## Review
_(uzupełnia Claude)_
