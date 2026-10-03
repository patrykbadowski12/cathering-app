# Dokumentacja projektu

| Gdzie | Co |
|---|---|
| [`board.md`](board.md) | Tablica zadań: statusy, kolejność, epiki |
| [`tasks/`](tasks/) | Jeden plik = jedno zadanie (cel, kryteria, wskazówki, review) |
| [`decisions/`](decisions/) | ADR, czyli zapisane decyzje architektoniczne i otwarte pytania |
| [`glossary.md`](glossary.md) | Słownik domeny (ubiquitous language) |

## Workflow zadania

```
TODO → IN PROGRESS → REVIEW → DONE
                       ↓
                   CHANGES (poprawki po review) → REVIEW
```

1. Bierzesz zadanie z góry kolumny TODO w `board.md` i zmieniasz status na `IN PROGRESS`.
2. Pracujesz. Notatki i pytania możesz dopisywać w sekcji **Notatki** w pliku zadania.
3. Gdy uznasz, że skończone: status `REVIEW` i piszesz do Claude'a „review T-00X”.
4. Claude dopisuje uwagi w sekcji **Review** pliku zadania i ustawia `DONE` albo `CHANGES`.

## Zasady zadań

- **Małe, ale z celem.** Każde zadanie kończy się czymś, co działa albo czymś, co zostało zdecydowane, a nie „połową refaktoru”.
- **Kryteria akceptacji są sprawdzalne.** Da się odpowiedzieć tak lub nie.
- **Wskazówki, nie gotowce.** Kod piszesz Ty, Claude robi review i tłumaczy dlaczego.
- Zadania z późniejszych epików rozpisujemy dopiero, gdy do nich dojdziemy, bo wtedy wiemy więcej.

## Zasady ADR

- Nowa decyzja to nowy plik `decisions/NNNN-tytul.md`. Starych plików nie edytujemy merytorycznie.
- Zmiana zdania to nowy ADR ze statusem `Accepted`, a stary dostaje `Superseded by NNNN`.
- Pytania jeszcze bez odpowiedzi trafiają do sekcji „Otwarte decyzje” w `decisions/README.md`.
