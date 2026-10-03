# 0002 — Spring MVC (blocking), bez WebFlux/korutyn w Fazach 1–3

**Status:** Accepted · **Data:** 2026-10-03

## Kontekst
Multi-tenancy (Faza 2) opiera się na `TenantContext` w `ThreadLocal`. W modelu reaktywnym request przeskakuje między wątkami i kontekst w `ThreadLocal` się gubi.

## Decyzja
Spring MVC (model wątek-na-request). Bez WebFlux i bez korutyn w warstwie web/persystencji w Fazach 1–3.

## Konsekwencje
- `ThreadLocal` działa przewidywalnie. Trzeba go jednak **zawsze czyścić** po requeście, bo pula wątków reużywa wątki.
- Do rozważenia później: virtual threads (Java 21, `spring.threads.virtual.enabled`). Zachowują model blokujący, ale wymagają sprawdzenia, jak zachowuje się `ThreadLocal`. To osobna, świadoma decyzja.
