# T-001 — Git i porządek w strukturze katalogów

**Status:** TODO · **Etap:** 0 · **Zależy od:** —

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

## Notatki
_(Twoje notatki / pytania)_

## Review
_(uzupełnia Claude)_
