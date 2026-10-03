# 0008 — Tożsamość: UUIDv7. Czytelność: slug

**Status:** Accepted · **Data:** 2026-10-03

## Kontekst
Rozważaliśmy, czy identyfikować tenanta (i inne obiekty) czytelną nazwą („Brokuł”) zamiast UUID, bo UUID utrudnia debugowanie. Nazwy jednak:
- się zmieniają (rebranding), a ID jest wskazywane przez klucze obce, zdarzenia, linki i logi,
- nie są unikalne między tenantami (dwa cateringi mogą mieć dietę „Keto”),
- są podatne na literówki, polskie znaki i wielkość liter.

Sekwencyjne ID (`1, 2, 3…`) zdradzają skalę biznesu i zachęcają do zgadywania cudzych zasobów.

## Decyzja
1. **Tożsamość każdego agregatu to UUID**, opakowany w typ domenowy (`TenantId`, `DietPlanId`, …). Tylko po nim wiążemy dane (klucze obce, zdarzenia, referencje między modułami).
2. **Wersja: UUIDv7** (prefiks ze znacznikiem czasu). Daje sortowalność po czasie utworzenia i lepszą lokalność indeksu B-tree w PostgreSQL przy dużych tabelach. Dla małych tabel (np. `tenants`) zysku praktycznie nie ma, ale stosujemy jedną zasadę w całym projekcie. Sposób generowania ustalamy w T-006.
3. **Czytelność zapewnia `slug`**, czyli osobne pole biznesowe (`[a-z0-9-]`), nigdy klucz:
   - Tenant: `slug` globalnie unikalny (np. `brokul`). Trafia do logów (MDC), a w Fazie 2 do domyślnej subdomeny i panelu.
   - Diety itd.: slug do URL-i i SEO, unikalny w obrębie tenanta. Wprowadzamy go w E1, gdy będzie potrzebny.
4. **`TenantContext` trzyma tylko `TenantId`**, bo kod potrzebuje tylko tożsamości. Slug tenanta trafia wyłącznie do MDC, bo służy ludziom.
5. **Faza 1:** domyślny tenant to para `(id, slug)` w `application.yaml`, z **ustalonym na stałe** UUIDv7. W Fazie 2 staje się pierwszym wierszem tabeli `tenants` z tym samym id, więc dane z Fazy 1 pasują bez migracji wartości.

## Konsekwencje
- Logi są czytelne (`[tenant=brokul]`), a baza i kod są odporne na zmiany nazw.
- `toString()` obiektów domenowych powinien zawierać nazwę obok ID.
- Generowanie v7 wymaga biblioteki albo własnej funkcji, bo standardowe `UUID.randomUUID()` daje v4. Do rozstrzygnięcia w T-006.
- Rozważone i odłożone: prefiksowane ID w stylu Stripe (`tnt_…`, `dp_…`).
