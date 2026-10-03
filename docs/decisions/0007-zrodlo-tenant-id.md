# 0007 — Źródło `tenantId`: Host → `TenantResolver` → `TenantContext`

**Status:** Accepted · **Data:** 2026-10-03 · **Wdrożenie:** T-004

## Kontekst
Docelowo (Faza 2) każdy catering ma własną domenę i tenant jest rozpoznawany z nagłówka `Host`. W Fazie 1 jest jeden tenant. Musimy zdecydować, czy przygotować „kształt” multi-tenancy już teraz (schemat, sygnatury), czy dodać go dopiero w Fazie 2.

Dodanie `tenant_id` później oznacza migrację wszystkich tabel z danymi, uzupełnienie istniejących wierszy i zmianę sygnatur w każdym module. Przygotowanie samego kształtu teraz kosztuje kilka małych klas.

## Decyzja (Faza 1)
- **`TenantId`** (value class) i **`TenantContext`** (`ThreadLocal`) w `shared-kernel`.
- **Servlet `Filter`** na brzegu aplikacji pyta interfejs **`TenantResolver`** o tenanta dla requestu, ustawia `TenantContext` i **zawsze** czyści go w `finally`.
- Jedyna implementacja w Fazie 1 to **`FixedTenantResolver`**, który zwraca domyślnego tenanta z `application.yaml`.
- **Przepływ odpowiedzialności:**
  - filtr *rozpoznaje* tenanta,
  - kontroler *bierze* `TenantId` z kontekstu i przekazuje go **jawnie** do use case'u,
  - domena *dostaje* `TenantId` jako parametr. Domena nigdy nie czyta `TenantContext` ani nie generuje `tenantId` sama.
- Kolumna `tenant_id` (`NOT NULL`) jest obecna od pierwszej migracji (T-006).

## Kierunek na Fazę 2 (ustalenia, jeszcze nie decyzje)
1. **`HostTenantResolver`** szuka po **pełnym hoście** w tabeli `tenant_domains(host → tenant_id)`, bez parsowania subdomeny. To od razu obsługuje własne domeny klientów (CNAME, np. `zamow.brokul.pl`), co jest argumentem sprzedażowym white-label.
2. **Nieznany host → 404**, nigdy domyślny tenant.
3. **Dev:** `*.localhost` (np. `brokul.localhost:8080`) działa w przeglądarkach bez edycji `hosts`. Nagłówek `X-Tenant-Id` jest dopuszczalny **wyłącznie w profilu dev**, a na produkcji nie ufamy nagłówkom ustawianym przez klienta.
4. **Panel właściciela:** tenant zalogowanego użytkownika musi się zgadzać z tenantem z hosta. Inaczej właściciel A edytuje dane B.
5. **Zdarzenia i async:** `ThreadLocal` nie przechodzi między wątkami ani przez kolejki, więc każde zdarzenie domenowe niesie `tenantId` w sobie.
6. **Logi:** filtr wkłada `tenant_id` do MDC.
7. **Za proxy (ALB/CloudFront):** host przychodzi w `X-Forwarded-Host`. Trzeba świadomie skonfigurować `server.forward-headers-strategy` przy deployu.
8. **Siatki bezpieczeństwa:** filtr Hibernate i PostgreSQL RLS czytają kontekst *niejawnie*. To celowy wyjątek od reguły „jawnie przez parametry”.

## Konsekwencje
- W Fazie 2 zmienia się tylko implementacja `TenantResolver` (plus nowa tabela). Domena, use case'y i schemat tabel biznesowych zostają bez zmian.
- Każda tabela biznesowa i każde zapytanie odczytujące dane musi uwzględniać `tenant_id`. Na razie pilnujemy tego ręcznie, a od Fazy 2 automatycznie (punkt 8).
- Trochę infrastruktury już w Fazie 1 (3–4 małe klasy).
