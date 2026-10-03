# 0005 — Domena bez zależności od Springa i JPA (porty i adaptery)

**Status:** Accepted · **Data:** 2026-10-03

## Kontekst
Tak jest już w `module-diet`: `DietPlan` i use case'y nie mają adnotacji Springa, repozytorium jest interfejsem (portem) w `domain`, a implementacja (adapter) siedzi w `infrastructure`. Use case'y są rejestrowane ręcznie w `DietModuleConfig`.

## Decyzja
- `domain`: czysty Kotlin. Bez `org.springframework.*` i `jakarta.persistence.*`.
- Encje JPA (`*Entity`) i model domenowy to osobne klasy z mapowaniem w adapterze.
- Beany use case'ów są rejestrowane w `*ModuleConfig` w warstwie `infrastructure`.

## Konsekwencje
- Domenę testuje się zwykłymi testami jednostkowymi, bez kontekstu Springa.
- Koszt: kod mapowania `Entity ↔ Domain`.
- Otwarte: jak zrobić transakcyjność, skoro use case nie zna Springa? (T-002)
- Na razie wymuszone tylko konwencją, a narzędziami dopiero od T-008.
