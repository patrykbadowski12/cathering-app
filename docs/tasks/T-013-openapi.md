# T-013 — Kontrakt API: OpenAPI generowane przez backend

**Status:** REVIEW · **Etap:** 0 · **Kto:** Ty (dokończył Claude) · **ADR:** 0011, 0012

## Cel
Backend publikuje opis swojego API w formacie OpenAPI. To źródło prawdy dla frontu, z którego w T-015 generujemy typowanego klienta TypeScript.

## Czego się uczysz
- *Contract-first* vs *code-first*: tu code-first, czyli kontrakt wynika z kontrolerów i DTO, ale jest publikowany i wersjonowany.
- Jak typy Kotlina (nullability, value classes, enumy) przekładają się na schemat OpenAPI i dlaczego ma to znaczenie dla frontu (pole `string` vs `string | null`).
- Swagger UI jako narzędzie developerskie.

## Kryteria akceptacji
- [x] Podział ścieżek wg ADR 0012: `GET /api/v1/shop/diet-plans`, `GET /api/v1/shop/diet-plans/{id}`, `POST /api/v1/admin/diet-plans` (`Location` w `admin`). Dwa kontrolery wołają te same use case'y. `docs/http/dietPlans.http` zaktualizowany.
- [x] Usunięte `spring-boot-starter-thymeleaf` z `module-diet` i `module-order`.
- [x] `GET /v3/api-docs` zwraca specyfikację z endpointami w obu strefach, tagi `DietPlanShop` / `DietPlanAdmin`.
- [x] Swagger UI lokalnie pod `/docs`. Decyzja o produkcji: wyłączyć w profilu produkcyjnym, gdy powstanie (E6).
- [x] Specyfikacja poprawna dla frontu: pola nienullowalne `required`, `state` z wartościami enuma, `id` jako `string` w formacie `uuid`, opisowe `operationId`, serwer względny `/`.
- [x] Odpowiedzi opisane kodami (`201`, `200`, `404` bez treści). `400` dojdzie w T-014.
- [x] Specyfikacja w repo (`docs/api/openapi.json`), pilnowana testem `OpenApiContractTest`: build pada, gdy API i plik się rozjadą. Odświeżenie: `./gradlew :app:test --tests '*OpenApiContractTest' -Dopenapi.update=true`.

## Wskazówki
- Wymagane pola z nullability Kotlina: konfiguracja Kotlina w springdoc włącza się tylko z modułem Kotlina dla **Jacksona 2** (`com.fasterxml.jackson.module:jackson-module-kotlin`), bo Swagger wewnętrznie używa Jacksona 2. Aplikacja sama używa Jacksona 3.
- Adnotacje Swaggera w modułach jako `compileOnly` (`swagger-annotations-jakarta`), w wersji zgodnej ze springdoc.
- Endpoint `/v3/api-docs` przechodzi przez `TenantFilter`. W Fazie 1 to nie problem, w Fazie 2 (host tenanta) trzeba to przemyśleć.
