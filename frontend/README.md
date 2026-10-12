# Frontend

React + TypeScript (Vite), ADR 0011. Strefy i adresy: ADR 0012.

## Uruchomienie

Wymagany Node `>=22.12` (`.nvmrc`). Backend musi działać na `localhost:8080`.

```bash
npm ci
npm run dev        # http://localhost:5173, /api/* przekierowane do Springa
```

## Skrypty

| Skrypt                    | Co robi                                          |
| ------------------------- | ------------------------------------------------ |
| `dev`                     | serwer deweloperski z hot reload                 |
| `build`                   | sprawdzenie typów + build produkcyjny do `dist/` |
| `preview`                 | podgląd buildu produkcyjnego                     |
| `typecheck`               | tylko sprawdzenie typów                          |
| `lint`                    | ESLint                                           |
| `format` / `format:check` | Prettier                                         |
| `test`                    | Vitest + Testing Library                         |

## Struktura

```
src/
├── app/        # konfiguracja aplikacji: router, QueryClient
├── shop/       # strefa sklepu (/): layout i strony
├── features/   # funkcje: API, hooki, komponenty (np. diet-plans/)
└── test/       # konfiguracja testów
```

Importy przez alias `@/` (= `src/`).
