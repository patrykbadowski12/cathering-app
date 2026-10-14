# 0013 — Wymuszanie granic modułów: Gradle + ArchUnit

**Status:** Accepted · **Data:** 2026-10-14 · **Wdrożenie:** T-008 · **Wymusza:** 0001, 0004, 0005, 0009

## Kontekst
Reguły z ADR 0001, 0004, 0005 i 0009 były dotąd pilnowane tylko na review. Rozważane opcje:
- **Submoduły Gradle per warstwa** (`module-diet:domain`, `:application`, …): najmocniejsze, bo pilnuje ich kompilator, ale to cztery pliki builda na moduł i dużo ceremonii przy jednej osobie w projekcie.
- **Spring Modulith:** weryfikuje moduły i daje rejestr publikacji zdarzeń (outbox), ale ma własną konwencję modułów i wymagałby konfiguracji „named interfaces” pod nasz podział warstw.
- **ArchUnit:** testy JUnit sprawdzające zależności w skompilowanym kodzie. Lekkie, jawne reguły 1:1 z naszymi ADR.

## Decyzja
Dwa mury:
1. **Gradle:** moduł widzi inny moduł tylko przez jawną zależność w `build.gradle.kts`. Dziś `module-diet`, `module-order` i `shared-kernel` nie zależą od siebie nawzajem (poza `shared-kernel`), więc kompilator blokuje takie importy.
2. **ArchUnit** (`app/src/test/.../architecture/ArchitectureTest.kt`, uruchamiany w `./gradlew build`):

| Reguła | Dozwolone zależności |
|---|---|
| `domain` | `domain`, `shared`, `java`, `kotlin`, adnotacje JetBrains (ADR 0005) |
| `application` | powyższe + `application` + `org.springframework.transaction` (ADR 0009) |
| `web` ↔ `infrastructure` | bez zależności w żadną stronę; kontroler idzie przez use case |
| między modułami | `application`/`infrastructure`/`web` innego modułu są niedostępne; do innego modułu tylko przez jego `domain` (ADR 0004) |
| `shared` | `shared`, `java`, `kotlin`, generator UUID (`com.github.f4b6a3`); bez Springa i modułów |

Testy żyją w `app`, bo tylko ten moduł widzi wszystkie klasy.

## Konsekwencje
- Złamanie reguły psuje build z komunikatem, która reguła i który import.
- Logowanie (SLF4J) **nie** jest dozwolone w `application` (decyzja otwarta w T-004). Logujemy w `web`/`infrastructure`. Gdy będzie potrzebne w use case'ach, zmieniamy regułę świadomie.
- Spring Modulith wraca do rozważenia przy E3 (zdarzenia między modułami, outbox).
- Nowy moduł działa z regułami automatycznie, pod warunkiem że trzyma konwencję pakietów `com.cateringplatform.<moduł>.(domain|application|infrastructure|web)`.
