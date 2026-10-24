# T-017 — CI: GitHub Actions dla backendu i frontendu

**Status:** TODO · **Etap:** A (razem z E1 albo zaraz po) · **Kto:** Ty

## Cel
Każdy push i PR do `main` automatycznie buduje backend i frontend oraz uruchamia testy i lintery. Czerwony pipeline blokuje merge.

## Czego się uczysz
- Składnia GitHub Actions: workflow, joby, kroki, `on: pull_request` vs `push`.
- Cache zależności (Gradle, npm) i ile czasu realnie oszczędza.
- Testcontainers w CI: czy runner GitHuba ma Dockera i co z tego wynika.
- Ochrona gałęzi (branch protection): wymagane checki przed merge.

## Kryteria akceptacji
- [ ] Workflow w `.github/workflows/` uruchamia się na PR i na push do `main`.
- [ ] Backend: `./gradlew check` (kompilacja, ktlint, testy z Testcontainers, ArchUnit, test kontraktu OpenAPI).
- [ ] Frontend: `npm ci`, `typecheck`, `lint`, `format:check`, `test`, `build`.
- [ ] Frontend sprawdza, czy wygenerowany klient API jest aktualny: `api:generate`, a potem brak zmian w `git diff`.
- [ ] Backend i frontend jako osobne joby, równolegle.
- [ ] Cache Gradle i npm; drugi przebieg jest wyraźnie szybszy od pierwszego.
- [ ] `main` chroniony: merge tylko przy zielonych checkach.

## Wskazówki
- Wersje narzędzi weź z repo, a nie z pamięci: JDK z `jvmToolchain` w `build.gradle.kts`, Node z `frontend/.nvmrc`.
- Sprawdź gotowe akcje do konfiguracji Javy i Gradle'a (mają wbudowany cache), zanim napiszesz cache ręcznie.
- Pierwszy przebieg pewnie się wywali. Czytaj logi kroku, a nie tylko status.
