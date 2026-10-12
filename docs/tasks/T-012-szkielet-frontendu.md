# T-012 — Szkielet frontendu w `frontend/`

**Status:** TODO · **Etap:** 0 · **Kto:** Claude · **ADR:** 0011

## Cel
W repozytorium jest działający projekt frontu: `npm run dev` startuje aplikację z przekierowaniem `/api/*` do Springa, a lint, formatowanie, typy i testy są skonfigurowane i przechodzą.

## Zakres
- `frontend/`: Vite + React + TypeScript (`strict`), Node w wersji LTS przypiętej (`.nvmrc` + `engines`).
- Tailwind CSS + shadcn/ui, React Router (strefy `/` i `/admin`, panel ładowany leniwie), TanStack Query.
- ESLint + Prettier, Vitest + Testing Library, jeden przykładowy test.
- Proxy dev: `/api` → `http://localhost:8080`.
- Skrypty: `dev`, `build`, `preview`, `lint`, `format`, `typecheck`, `test`.
- `frontend/README.md`: jak uruchomić; sekcja w `docs/README.md`.
- `.gitignore` dla `node_modules/`, `dist/`; `.editorconfig` obejmuje pliki frontu.

## Kryteria akceptacji
- [ ] `npm ci && npm run lint && npm run typecheck && npm run test && npm run build` przechodzi.
- [ ] `npm run dev` + działający backend: strona startowa sklepu i pusta strona `/admin` się otwierają.
- [ ] Request z przeglądarki do `/api/v1/diet-plans` przechodzi przez proxy (ten sam origin, bez CORS) i w logu backendu widać tenanta.
- [ ] Struktura katalogów opisana w `frontend/README.md` (gdzie strony, komponenty, klient API).

## Review
_(uzupełnia Claude; akceptacja: Ty)_
