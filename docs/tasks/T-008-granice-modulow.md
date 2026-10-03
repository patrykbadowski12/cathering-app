# T-008 — Decyzja i implementacja: wymuszenie granic modułów

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-002 · **ADR:** 0001, 0004, 0005

## Cel
Złamanie reguł zależności (np. `domain` importuje JPA, `module-order` importuje `diet.infrastructure`) **psuje build**, a nie jest tylko łapane na review.

## Czego się uczysz
- Różne poziomy wymuszania architektury: kompilator vs testy architektury vs framework.
- Spring Modulith: moduły aplikacyjne, `ApplicationModules.verify()`, dokumentacja modułów, Event Publication Registry (temat do E3).

## Część 1: dyskusja
| Opcja | Na czym polega | Do przemyślenia |
|---|---|---|
| A. Gradle submoduły | `module-diet:domain`, `:infrastructure`, `:web` jako osobne projekty | Najmocniejsze (kompilator), ale dużo plików build i więcej „ceremonii” |
| B. ArchUnit | Test sprawdzający reguły importów między pakietami | Elastyczne i lekkie, ale łamanie wychodzi dopiero w testach |
| C. Spring Modulith | Framework weryfikujący granice modułów i wspierający zdarzenia | Dużo daje „za darmo”, ale ma własne konwencje pakietów, sprawdź zgodność z Boot 4 |
| Kombinacja | np. moduły Gradle per bounded context + ArchUnit dla warstw wewnątrz modułu | Często najlepszy kompromis |

## Kryteria akceptacji
- [ ] Nowy ADR (kolejny wolny numer) o wymuszaniu granic modułów.
- [ ] Reguły sprawdzane automatycznie: (1) `domain` nie zależy od Springa ani JPA, (2) żaden moduł nie importuje `infrastructure`/`web` innego modułu.
- [ ] Demonstracja: celowo złam regułę, pokaż, że build/test pada, a potem wycofaj zmianę.

## Review
_(uzupełnia Claude)_
