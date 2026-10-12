# 0012 — Układ adresów: sklep, panel i API pod domeną tenanta

**Status:** Accepted · **Data:** 2026-10-11 · **Doprecyzowuje:** 0007, 0011

## Kontekst
Frontend to jedna aplikacja React (ADR 0011) obsługująca dwie grupy użytkowników: klienta końcowego (sklep) i właściciela cateringu (panel). Tenant jest rozpoznawany po hoście (ADR 0007). Ukrycie ekranów panelu w UI nie jest zabezpieczeniem, więc ochrona musi być na poziomie API.

## Decyzja
**Jedna domena per tenant, trzy strefy:**

| Ścieżka | Co | Dostęp |
|---|---|---|
| `/` | sklep (lista diet, jadłospis, formularz zamówienia) | publiczny |
| `/admin` | panel właściciela (zamówienia, lista produkcyjna, diety, jadłospisy) | zalogowany właściciel tego tenanta |
| `/api/v1/shop/...` | publiczne API sklepu: odczyty + zgłoszenie zamówienia | publiczny |
| `/api/v1/admin/...` | API panelu | sesja właściciela; tenant użytkownika = tenant z hosta |

- Faza 1: jedna domena (jeden tenant). Faza 2: ten sam układ na każdej domenie tenanta (`<slug>.catering.pl`, własne domeny klientów).
- Sklep i panel to **jedna** aplikacja React z dwiema strefami routingu. Kod panelu ładowany leniwie, więc klient sklepu go nie pobiera.
- Na produkcji `/api/*` trafia do Springa, a każda inna ścieżka do plików frontu (`index.html` jako fallback dla tras SPA).
- Lokalnie: Vite (`localhost:5173`) serwuje front i przekazuje `/api/*` do Springa (`localhost:8080`).
- Spring Security (E4): jedna reguła `/api/v1/admin/** → authenticated` plus sprawdzenie zgodności tenanta użytkownika z tenantem z hosta.

## Konsekwencje
- Obecne `/api/v1/diet-plans` w E1 rozdziela się na odczyty w `shop` i zarządzanie w `admin`. DTO mogą się różnić (np. sklep nie widzi diet nieaktywnych ani pól wewnętrznych).
- Ten sam use case może być wołany z obu stref; różnią się tylko adaptery webowe.
- Panel platformy (zarządzanie tenantami przez operatora, np. `app.catering.pl`) to osobna decyzja na Fazę 3.
