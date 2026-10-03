# T-001 — Git i porządek w strukturze katalogów

**Status:** DONE · **Etap:** 0 · **Zależy od:** —

## Cel
Jedno czyste repozytorium git z projektem w katalogu głównym, bez zagnieżdżenia `catering-platform/catering-platform`.

## Czego się uczysz
- Higiena repozytorium: co commitować, a co ignorować.
- Czytelne commity, np. [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `chore:`, `docs:`).

## Kryteria akceptacji
- [ ] Katalog główny repo zawiera bezpośrednio `settings.gradle.kts`, `gradlew` i `docs/`.
- [ ] Nie ma zdublowanych `.idea/` ani `build/` w katalogu nadrzędnym.
- [ ] `architektura-techniczna.md` i `plan-projektu-fazy.md` są w `docs/` (wersjonowane razem z kodem).
- [ ] Po `./gradlew build` polecenie `git status` jest czyste (ignorowane są `build/`, `.gradle/`, `.idea/`, `.kotlin/`).
- [ ] `HELP.md` (wygenerowany przez Spring Initializr) jest usunięty albo zastąpiony krótkim `README.md`.
- [ ] Pierwszy commit (opcjonalnie: repo na GitHubie, przyda się w E5).

## Wskazówki
- Zanim cokolwiek usuniesz, sprawdź, który katalog IntelliJ ma otwarty jako projekt (**File → Recent Projects**). Po przeniesieniu otwórz projekt na nowo.
- `.gitignore` już ignoruje `.idea`. Zastanów się, czy chcesz współdzielić część ustawień IDE (np. code style). Na razie prościej ignorować całość.
- Uwaga: `./gradlew build` może jeszcze nie przechodzić, bo kompilację naprawiasz w T-002. Sprawdź `git status` po buildzie, nawet nieudanym.

## Review

### Runda 1 — 2026-10-03 · commit `8e0a3d3` → **CHANGES** (drobne)

| Kryterium | Wynik |
|---|---|
| Projekt w katalogu głównym repo, bez zagnieżdżenia | ✅ |
| Brak zdublowanych `.idea/`/`build/` w katalogu nadrzędnym | ✅ (zostały tylko stare kopie dwóch plików .md, do usunięcia po commicie) |
| `architektura-techniczna.md` i `plan-projektu-fazy.md` w `docs/` | ❌ brak w commicie |
| `build/`, `.gradle/`, `.idea/`, `.kotlin/` ignorowane | ✅ (`git check-ignore` potwierdza) |
| `HELP.md` usunięty | ✅ |
| Pierwszy commit | ✅ · GitHub: remote ustawiony, ale nic nie jest wypchnięte (`main` nie śledzi `origin/main`) |

**Do poprawy:**
1. Przenieś oba dokumenty planu do `docs/`. To nasze źródło prawdy dla ADR-ów, więc powinny być wersjonowane razem z kodem.
   → ✅ naprawione (pliki w `docs/`; `architektura-techniczna.md` dostała nagłówek „przy konflikcie wygrywa ADR”). Sekcja Notatki usunięta ze wszystkich zadań, bo ustalenia z rozmowy Claude zapisuje w Review/ADR.
2. (opcjonalne) `git push -u origin main`. Od tego momentu pracujemy na gałęziach i PR-ach (patrz `docs/README.md`, sekcja Git).

**Uwagi (bez wpływu na status):**
- Commit na `main` jest **w porządku**. Pierwszy commit w repo zawsze trafia na gałąź domyślną. Gałęzie zaczynamy od T-002.
- Wiadomość `T-001 : Git init and catalog structure`: ID zadania w commicie to dobry nawyk, bo łączy kod z taskiem. Proponuję jeden format na stałe, np. `chore(T-001): init repository and project structure` (Conventional Commits + ID zadania). Wtedy `git log` czyta się jak changelog.
- „catalog” to po angielsku raczej *katalog produktów*. Folder to *directory*.

### Runda 2 — 2026-10-03 → **DONE**
Dokumenty planu są w `docs/`, katalog nadrzędny jest czysty. Push na GitHub nastąpi z finalnym commitem T-001.
