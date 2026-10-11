# T-010 — ktlint i `.editorconfig`: jeden styl kodu, bez wildcard importów

**Status:** DONE · **Etap:** 0 · **Zależy od:** — · **Wykonanie:** Claude (na prośbę: „raz zrobione i zapomniane”)

## Cel
Styl kodu jest sprawdzany automatycznie: `./gradlew build` pada przy naruszeniu (np. `import foo.*`), a `./gradlew ktlintFormat` naprawia większość rzeczy sam. IntelliJ formatuje zgodnie z tymi samymi regułami, więc wildcard importy przestają się pojawiać.

## Czego się uczysz
- Różnica między **formatterem/linterem** (ktlint: wygląd kodu) a **analizą statyczną** (detekt: zapachy kodu, złożoność). Na teraz wystarczy ktlint.
- `.editorconfig` jako wspólne źródło ustawień dla IDE i narzędzi.
- Podpinanie zadań weryfikujących pod `check`, żeby build był jedynym strażnikiem.
- Dlaczego reformat całego repo robi się **osobnym commitem**: inaczej review każdego zadania tonie w zmianach białych znaków.

## Kryteria akceptacji
- [ ] Plugin ktlint dla Gradle zastosowany do **wszystkich** modułów (w `subprojects {}` w głównym `build.gradle.kts`), z jawnie przypiętą wersją.
- [ ] `.editorconfig` w katalogu głównym:
  - ustawienia dla `*.{kt,kts}`,
  - wybrany styl ktlint (`ktlint_code_style`),
  - IntelliJ **nigdy** nie zwija importów do `*`.
- [ ] `./gradlew build` uruchamia sprawdzenie ktlint (przez `check`) i pada, gdy w kodzie jest wildcard import. Pokaż to: dodaj jeden celowo, zobacz błąd, usuń.
- [ ] Cały istniejący kod przeformatowany `ktlintFormat` w **osobnym commicie** (`style(T-010): apply ktlint formatting`), bez zmian logiki.
- [ ] IntelliJ: po *Optimize Imports* / *Reformat Code* nie pojawiają się wildcardy.
- [ ] Krótka sekcja w `docs/README.md`: jak sprawdzić i naprawić styl przed commitem.

## Wskazówki
- Dwa popularne sposoby:
  - plugin `org.jlleitschuh.gradle.ktlint` (najprostszy, daje `ktlintCheck` i `ktlintFormat`),
  - **Spotless** (`com.diffplug.spotless`), który obsługuje wiele formatterów naraz.

  Do samego Kotlina wystarczy ten pierwszy. Aktualną wersję sprawdź na Gradle Plugin Portal.
- IntelliJ czyta `.editorconfig` i rozumie klucze z prefiksem `ij_kotlin_…`. Szukaj kluczy, które sterują progiem, od którego IDE zamienia importy na `*` (dla nazw z pakietu i dla członków klasy).
- Styl `ktlint_official` jest najsurowszy. `intellij_idea` jest bliższy temu, co IDE robi domyślnie. Wybierz świadomie, bo `ktlint_official` zmieni więcej w istniejącym kodzie.
- Pliki `.kts` (build skrypty) też podlegają ktlintowi.
- Opcjonalnie: git hook `pre-commit` z `ktlintCheck`. Plugin potrafi go zainstalować sam. Rozważ, czy chcesz, żeby blokował commit.

## Review

### Wykonanie — 2026-10-11 · gałąź `task/T-010-ktlint` → **DONE**

**Co zostało ustawione:**
- Plugin `org.jlleitschuh.gradle.ktlint` 14.2.0 i silnik ktlint **1.8.0**, w `allprojects` w głównym `build.gradle.kts` (sprawdza też `*.gradle.kts` wszystkich modułów). Ręcznie dodany wpis w `app/build.gradle.kts` usunięty.
- `.editorconfig`:
  - styl `intellij_idea`, `max_line_length = 120`, wcięcie 4 (YAML: 2),
  - klasy z 2+ parametrami konstruktora mają parametry w osobnych liniach,
  - IntelliJ nigdy nie zwija importów do `*`.
- `.gitattributes`: `* text=auto eol=lf`, czyli LF w repo i w kopii roboczej. Na Windows z `core.autocrlf=true` zapobiega to fałszywym błędom ktlint po checkoutcie.
- `shared-kernel/build.gradle.kts`: komentarz zamiast pustego pliku (ktlint odrzuca puste skrypty).

**Decyzje warte zapamiętania:**
- **`ij_kotlin_packages_to_use_import_on_demand =` (pusta wartość), a nie `unset`.** Przy `unset` ktlint zachowuje domyślną listę wyjątków, w której jest `java.util.*`, więc pierwszy test z `import java.util.*` przechodził. Pusta wartość blokuje wszystkie wildcardy. Ostrzejsza reguła od razu znalazła ukryty `java.util.*` w `CreateDietPlanUseCaseTest`.
- **Pakiet bazowy przemianowany:** `com.catering_platform` → `com.cateringplatform` (konwencja: same małe litery, bez podkreśleń), na prośbę z 2026-10-11. Reguła `package-name` jest znowu włączona. Zmienione: katalogi źródeł, deklaracje `package` i importy, `basePackages` w `CateringPlatformApplication` oraz `group` w `build.gradle.kts`. Zweryfikowane czystym buildem (19 testów) i `bootRun` (POST `201`, GET `200`, log z tenantem).

**Weryfikacja:**
- `./gradlew build` ✅, 19 testów zielonych.
- `import java.util.*` w `TenantId.kt` → `./gradlew build`: `TenantId.kt:3:1 Wildcard import` i **BUILD FAILED** ✅ (zmiana wycofana).
- Pozostałe wildcardy z kodu (`jakarta.persistence.*`, `java.util.*`, `Assertions.*`) zamienione na jawne importy.
- Formatowanie (`ktlintFormat`) bez zmian logiki: przecinki na końcu list, sygnatury w jednej lub wielu liniach, `{ return x }` → `= x`, znak nowej linii na końcu plików.

**Commity na gałęzi:** konfiguracja (`build`), formatowanie (`style`) i dokumentacja (`docs`) osobno. Uwaga: *Squash and merge* sklei je na `main` w jeden commit. Przy tym PR rozważ *Rebase and merge*, żeby reformat został osobnym commitem w historii (łatwiej go wtedy pominąć w `git blame`).
