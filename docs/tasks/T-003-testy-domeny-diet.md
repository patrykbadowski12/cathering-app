# T-003 — Testy jednostkowe domeny `DietPlan`

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-002

## Cel
Reguły biznesowe `DietPlan` i use case'ów są pokryte szybkimi testami jednostkowymi, bez kontekstu Springa i bez bazy.

## Czego się uczysz
- Testowanie domeny w izolacji: to jest główna korzyść z ADR 0005.
- **Fake vs mock:** prosta implementacja `DietPlanRepository` w pamięci zamiast biblioteki do mocków. Kiedy które ma sens?
- Struktura testu: given / when / then, nazwy testów opisujące zachowanie.

## Kryteria akceptacji
- [ ] Testy w `module-diet/src/test/kotlin/...` (moduł potrzebuje zależności testowych w `build.gradle.kts`).
- [ ] Pokryte reguły: pusta nazwa jest odrzucana, `kcal <= 0` jest odrzucane, nowy plan ma stan `ACTIVE`.
- [ ] `CreateDietPlanUseCase` i `GetDietPlansUseCase` są przetestowane z fake'owym repozytorium, w tym to, że `GetDietPlans` zwraca tylko plany danego tenanta.
- [ ] `./gradlew :module-diet:test` przechodzi i trwa sekundy, nie dziesiątki sekund.

## Wskazówki
- Nazwy testów w Kotlinie mogą być zdaniami w backtickach, np. ``fun `should reject blank name`()``.
- Do asercji wystarczy `kotlin.test`. Możesz też spróbować AssertJ albo Kotest assertions i ocenić, co czyta się lepiej.

## Review
_(uzupełnia Claude)_
