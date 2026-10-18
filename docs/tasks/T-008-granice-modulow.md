# T-008 — Decyzja i implementacja: wymuszenie granic modułów

**Status:** DONE · **Etap:** 0 · **Zależy od:** T-002 · **ADR:** 0001, 0004, 0005, 0009, 0013 · **Wykonanie:** Claude (na prośbę)

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
- [x] ADR 0013: Gradle (zależności między modułami) + ArchUnit (`app/src/test/.../architecture/ArchitectureTest.kt`).
- [x] Reguły sprawdzane automatycznie w `./gradlew build` (5 testów, dodatkowo: `web` ↔ `infrastructure` i czysty shared kernel):
  1. `domain` nie zależy od niczego (ani Spring, ani JPA, ani `application`),
  2. `application` zależy tylko od `domain` i `org.springframework.transaction` (ADR 0009),
  3. żaden moduł nie importuje `application`/`infrastructure`/`web` innego modułu.
- [x] Demonstracja: tymczasowe pliki łamiące każdą z 5 reguł (Spring w `domain`, SLF4J w `application`, `web` → `infrastructure`, `app/tenant` → `diet.infrastructure`, Spring w `shared` po dopisaniu zależności) → każdy test pada na swoim naruszeniu; po usunięciu plików zielono.
