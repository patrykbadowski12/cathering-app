# 0001 — Modularny monolit, moduł Gradle per bounded context

**Status:** Accepted · **Data:** 2026-10-03

## Kontekst
Projekt robi jedna osoba po godzinach. Mikroserwisy od startu oznaczają dużo infrastruktury (sieć, deploy, tracing) bez realnej korzyści. Chcemy jednak, żeby ewentualne wydzielenie serwisu w przyszłości było mechaniczne, a nie przepisywaniem logiki.

## Decyzja
Jedna aplikacja (jeden deploy, jedna baza), podzielona na moduły Gradle per bounded context: `module-diet`, `module-order`, później `module-tenant`, `module-billing`, `module-logistics`. Moduł `app` spina całość. Kod wspólny trafia do `shared-kernel`.

Serwis wydzielamy dopiero przy konkretnym sygnale: inny profil skalowania, potrzeba niezależnych wdrożeń albo osobny zespół.

## Konsekwencje
- Prosty deploy i debugowanie.
- Granice trzeba pilnować dyscypliną i narzędziami (patrz T-008), bo jedna JVM pozwala na „skróty”.
- Szczegóły: `architektura-techniczna.md`, sekcje 2 i 4 (po T-001 w `docs/`).
