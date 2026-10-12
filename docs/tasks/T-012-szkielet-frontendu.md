# T-012 — Szkielet frontendu w `frontend/`

**Status:** DONE · **Etap:** 0 · **Kto:** Claude · **ADR:** 0011, 0012

## Cel
W repozytorium jest działający projekt frontu: `npm run dev` startuje sklep z przekierowaniem `/api/*` do Springa, a lint, formatowanie, typy i testy są skonfigurowane i przechodzą.

## Zakres
- `frontend/`: Vite + React + TypeScript (`strict`), Node przypięty (`.nvmrc` + `engines`).
- Tailwind CSS, React Router, TanStack Query.
- ESLint + Prettier, Vitest + Testing Library.
- Proxy dev: `/api` → `http://localhost:8080`.
- Strona sklepu z listą diet pobieraną z obecnego API.
- **Poza zakresem (decyzja 2026-10-12):** strefa `/admin` (na razie niepotrzebna) i shadcn/ui (dojdzie wraz z pierwszym formularzem w T-015).

## Kryteria akceptacji
- [x] `npm ci && npm run lint && npm run typecheck && npm run test && npm run build` przechodzi.
- [x] `npm run dev` + działający backend: strona sklepu się otwiera i pokazuje diety z backendu.
- [x] Request z przeglądarki do `/api/v1/diet-plans` przechodzi przez proxy (ten sam origin, bez CORS) i w logu backendu widać tenanta.
- [x] Struktura katalogów opisana w `frontend/README.md`.

## Review

### Wykonanie — 2026-10-12 · gałąź `task/T-012-frontend-skeleton` → **REVIEW** (niezacommitowane, do Twojej akceptacji)

**Stack** (najnowsze stabilne wersje w dniu instalacji): React 19, TypeScript 6 (`strict`, `noUncheckedIndexedAccess`), Vite 8, Tailwind 4, React Router 7, TanStack Query 5, ESLint 10 + typescript-eslint, Prettier 3 (+ sortowanie klas Tailwind), Vitest 5 + Testing Library + jsdom.

**Co widać w przeglądarce (`http://localhost:5173`):**
- nagłówek, sekcja powitalna z przyciskiem „Zobacz diety”,
- siatka kart z aktywnymi dietami (nazwa, kcal dziennie, przycisk „Zamów” na razie wyłączony),
- stany: ładowanie (szkielety kart), pusta lista, błąd z przyciskiem „Spróbuj ponownie”,
- strona 404 dla nieznanych adresów.

**Struktura:** `src/app` (router, QueryClient), `src/shop` (layout i strony sklepu), `src/features/diet-plans` (API, hook, komponent, test). Alias `@/` = `src/`.

**Weryfikacja:**
- `format:check`, `lint`, `typecheck`, `test` (3 testy: lista aktywnych diet bez nieaktywnych, pusta lista, błąd), `build` ✅.
- End-to-end na tymczasowym Postgresie:
  - backend `bootRun` + 4 diety przez POST,
  - Vite: `GET localhost:5173/` → HTML aplikacji,
  - `GET localhost:5173/api/v1/diet-plans` → `200` z danymi z backendu (proxy),
  - log backendu: `INFO [tenant=testdieta]`,
  - nieznana trasa → `200` (fallback SPA, 404 renderuje React).

**Świadome uproszczenia (do zastąpienia):**
- `dietPlansApi.ts`: ręczny `fetch` + typ `DietPlan` z `TODO(T-015)`. Zastąpi go klient generowany z OpenAPI (T-013 → T-015). Ścieżka zmieni się na `/api/v1/shop/diet-plans` (ADR 0012).
- Filtrowanie aktywnych diet jest na froncie, bo obecne API zwraca wszystkie. W `shop` API powinno zwracać tylko aktywne (E1).
- Nazwa „Catering” i kolor marki na sztywno (`--color-brand` w `index.css`). W Fazie 2 przyjdą z `theme_config` tenanta. CSS variables są przygotowane pod to.

**Zmiana poza `frontend/`:** `.editorconfig` dostał wcięcie 2 dla plików frontu (`ts`, `tsx`, `js`, `json`, `css`, `html`), zgodnie z konwencją JS/TS. Prettier i IntelliJ czytają ten plik.

### Runda 2 — 2026-10-12 · dopracowanie wyglądu (na prośbę) → **REVIEW**
- **Nowe zależności:** `lucide-react` (ikony; standard używany przez shadcn/ui) i `@fontsource-variable/inter` (font serwowany lokalnie, bez Google Fonts, więc bez zewnętrznych zapytań).
- **Nagłówek:** przyklejony (`sticky`), z rozmyciem tła, logo z ikoną, nawigacja i przycisk „Wybierz dietę”.
- **Hero:** dwie kolumny, karta „Przykładowy dzień” (ilustracja, dane przykładowe), trzy wyróżniki z ikonami, delikatna animacja wejścia (wyłączana przy `prefers-reduced-motion`).
- **Karty diet:** wydzielony `DietPlanCard`, kolorowy pasek akcentu, ikona przy kcal, uniesienie przy najechaniu.
- **Nowa sekcja „Jak to działa”** (3 kroki).
- Rozbudowana stopka, favicon, `meta description` i `theme-color`, płynne przewijanie do sekcji z marginesem pod przyklejony nagłówek.
- **`.gitignore` frontu** uzupełniony o `.env*.local`, cache Vite i ESLinta oraz logi menedżerów pakietów.

**Weryfikacja:**
- `format:check`, `lint`, `typecheck`, `test` (3), `build` ✅.
- Render w headless Chrome na prawdziwym backendzie: wszystkie sekcje i 4 diety z API. Zrzuty desktop (1366 px) i mobile (390 px) bez przepełnień.
- Po doinstalowaniu pakietów przy działającym `npm run dev` przeglądarka zgłosiła `Cannot read properties of null (reading 'useContext')`. To nieaktualny cache zależności Vite (dwie kopie Reacta w przeglądarce), a nie błąd w kodzie: `npm ls react` pokazuje jedną wersję. Rozwiązanie: restart `npm run dev` (ewentualnie z `--force`).

### Zamknięcie — 2026-10-12 → **DONE**
Zaakceptowane przez właściciela projektu.
