# Frontend

React + TypeScript (Vite), ADR 0011. Strefy i adresy: ADR 0012.

## Uruchomienie

Wymagany Node `>=22.12` (`.nvmrc`). Backend musi działać na `localhost:8080`.

```bash
npm ci
npm run dev        # http://localhost:5173, /api/* przekierowane do Springa
```

Sklep: `/`, panel właściciela: `/admin` (ładowany leniwie, na razie bez logowania, E4).

## Skrypty

| Skrypt                    | Co robi                                           |
| ------------------------- | ------------------------------------------------- |
| `dev`                     | serwer deweloperski z hot reload                  |
| `build`                   | sprawdzenie typów + build produkcyjny do `dist/`  |
| `preview`                 | podgląd buildu produkcyjnego                      |
| `typecheck`               | tylko sprawdzenie typów                           |
| `lint`                    | ESLint                                            |
| `format` / `format:check` | Prettier                                          |
| `test`                    | Vitest + Testing Library, API mockowane przez MSW |
| `api:generate`            | typy klienta API z `docs/api/openapi.json`        |

## Struktura

```
src/
├── api/        # klient API (openapi-fetch) i wygenerowane typy (schema.d.ts)
├── app/        # konfiguracja aplikacji: router, QueryClient
├── admin/      # strefa panelu (/admin): layout i strony
├── shop/       # strefa sklepu (/): layout i strony
├── features/   # funkcje: API, hooki, komponenty (np. diet-plans/)
└── test/       # konfiguracja testów
```

Importy przez alias `@/` (= `src/`).

## Klient API

Typy są generowane z kontraktu w `docs/api/openapi.json` (źródło prawdy, ADR 0011). Po zmianie API na backendzie:

1. odśwież kontrakt: `./gradlew :app:test --tests '*OpenApiContractTest' -Dopenapi.update=true`,
2. wygeneruj typy: `npm run api:generate`,
3. popraw miejsca, w których `npm run typecheck` zgłasza błędy.

`src/api/schema.d.ts` jest generowany, nie edytuj go ręcznie. Wywołania API idą przez `apiClient` z `src/api/client.ts`, a błędy (`ProblemDetail`) przez `ApiError`.
