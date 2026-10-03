# Dokumentacja projektu

| Gdzie | Co |
|---|---|
| [`board.md`](board.md) | Tablica zadań: statusy, kolejność, epiki |
| [`tasks/`](tasks/) | Jeden plik = jedno zadanie (cel, kryteria, wskazówki, review) |
| [`decisions/`](decisions/) | ADR, czyli zapisane decyzje architektoniczne i otwarte pytania |
| [`glossary.md`](glossary.md) | Słownik domeny (ubiquitous language) |
| [`plan-projektu-fazy.md`](plan-projektu-fazy.md) | Wizja produktu, strategia, fazy i ich definicje „gotowe” |
| [`architektura-techniczna.md`](architektura-techniczna.md) | Architektura docelowa (przy konflikcie wygrywa ADR) |

## Workflow zadania

```
TODO → IN PROGRESS → REVIEW → DONE
                       ↓
                   CHANGES (poprawki po review) → REVIEW
```

1. Bierzesz zadanie z góry kolumny TODO w `board.md` i zmieniasz status na `IN PROGRESS`.
2. Pracujesz. Pytania i wątpliwości omawiamy w rozmowie, a ważne ustalenia Claude zapisuje w Review zadania albo w ADR.
3. Gdy uznasz, że skończone: status `REVIEW` i piszesz do Claude'a „review T-00X”.
4. Claude dopisuje uwagi w sekcji **Review** pliku zadania i ustawia `DONE` albo `CHANGES`.

## Git: gałąź per zadanie

`main` zawsze się buduje i zawiera tylko zrecenzowane zmiany. Każde zadanie robisz na osobnej gałęzi:

```bash
git switch main && git pull                     # świeży main
git switch -c task/T-002-diet-path              # nowa gałąź dla zadania
# ... praca, małe commity:
git commit -m "fix(T-002): implement findAllByTenantId"
git push -u origin task/T-002-diet-path         # pierwszy push gałęzi
# → PR na GitHubie: task/T-002-... → main, status zadania: REVIEW
# → po review: merge PR (Squash and merge), usunięcie gałęzi
git switch main && git pull                     # wracasz na zaktualizowany main
```

- **Nazwa gałęzi:** `task/T-NNN-krotki-opis`
- **Commity:** `typ(T-NNN): opis` (typy: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `build`)
- **Review:** Claude czyta diff gałęzi względem `main` (`git diff main...task/T-NNN-...`), a uwagi trafiają do pliku zadania.
- **Merge:** *Squash and merge*, czyli jedno zadanie = jeden commit na `main`. Historia `main` jest czytelna, a na gałęzi możesz commitować „brudno”.

## Zasady zadań

- **Małe, ale z celem.** Każde zadanie kończy się czymś, co działa albo czymś, co zostało zdecydowane, a nie „połową refaktoru”.
- **Kryteria akceptacji są sprawdzalne.** Da się odpowiedzieć tak lub nie.
- **Wskazówki, nie gotowce.** Kod piszesz Ty, Claude robi review i tłumaczy dlaczego.
- Zadania z późniejszych epików rozpisujemy dopiero, gdy do nich dojdziemy, bo wtedy wiemy więcej.

## Zasady ADR

- Nowa decyzja to nowy plik `decisions/NNNN-tytul.md`. Starych plików nie edytujemy merytorycznie.
- Zmiana zdania to nowy ADR ze statusem `Accepted`, a stary dostaje `Superseded by NNNN`.
- Pytania jeszcze bez odpowiedzi trafiają do sekcji „Otwarte decyzje” w `decisions/README.md`.
