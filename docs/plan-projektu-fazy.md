# 🎯 Portal SaaS dla Cateringów — Plan Projektu w Fazach

Cel projektu: nauka (multi-tenancy, DDD, Kotlin/Spring Boot, AWS) + realny, używany produkt, z ewentualnym skromnym zyskiem. Model: **white-label**, nie marketplace — każdy catering dostaje własną markową subdomenę (np. `brokul.catering.pl`), a nie miejsce na wspólnej liście jak w Dietly.

---

## Faza 0 — Walidacja (przed kodem)

**Biznes:**
- Landing page: *"Własna strona dla Twojego cateringu. Twoja marka, Twoja domena, bez prowizji od zamówień."*
- Formularz zbierający maile zainteresowanych właścicieli cateringów
- Promocja: grupy FB dla branży cateringowej, fora, bezpośrednie wiadomości do 20–30 cateringów
- Sygnał do dalszej pracy: realne zainteresowanie po 2–3 tygodniach; brak zainteresowania = zmień przekaz/grupę docelową

**Tech:** jedna prosta strona statyczna (HTML/CSS, Carrd, Notion) — to test rynku, nie część produktu.

---

## Faza 1 — MVP jednotenantowe

**Cel biznesowy:** jeden w pełni działający sklep dla jednej, realnej firmy (własnej testowej albo pierwszego chętnego z Fazy 0). Sprawdzenie całego cyklu: klient wchodzi → wybiera dietę → zamawia → catering realizuje.

**Zakres funkcjonalny:**
- Katalog diet (nazwa, opis, kalorie, cena)
- Jadłospis na dany dzień/tydzień
- Koszyk + formularz zamówienia (bez płatności online na start — nawet telefon/przelew ręczny wystarczy)
- Panel właściciela: dodawanie/edycja diet i jadłospisów

**Architektura (wysoki poziom):** Spring Boot + Kotlin, jedna aplikacja, jedna baza, bez multi-tenancy. Moduły: `diet`, `order`, `admin`. PostgreSQL + Flyway. Thymeleaf (SSR).

**Definicja "gotowe":** ktoś realnie złożył zamówienie przez tę stronę i catering je zrealizował.

---

## Faza 2 — Multi-tenancy i customizacja JSON (rdzeń pomysłu)

**Cel biznesowy:** 2–3 różne cateringi na tym samym kodzie, każdy pod swoją subdomeną, każdy z innym wyglądem.

**Architektura (wysoki poziom):**
- `TenantInterceptor` rozpoznający tenanta po subdomenie
- Wildcard DNS (`*.catering.pl`) + wildcard SSL
- `tenant_id` w każdej tabeli biznesowej (row-level isolation)
- `theme_config` (jsonb) w tabeli `tenants`: kolory, logo, treści hero, kolejność sekcji
- Renderowanie: CSS custom properties wstrzykiwane z JSON-a przy SSR

**Definicja "gotowe":** dwóch różnych właścicieli cateringów zmienia kolory/logo w swoim panelu i widzi efekt na swojej subdomenie, bez wpływu na innych tenantów.

---

## Faza 3 — Panel admina i samoobsługa

**Cel biznesowy:** właściciel cateringu sam zarządza swoją stroną bez Twojej pomocy — warunek konieczny do skalowania powyżej 2–3 klientów obsługiwanych ręcznie. **Tu też dobry moment, żeby zacząć pobierać pierwszy realny abonament** (np. 50–150 zł/mies. za tenanta).

**Zakres funkcjonalny:**
- Formularz edycji `theme_config` (color picker, upload logo, edycja tekstów) zamiast ręcznego JSON-a
- Podgląd na żywo przed zapisaniem
- Walidacja configu (JSON Schema) — błędny wpis nie może rozwalić strony

---

## Faza 4 — Płatności i zamówienia z prawdziwego zdarzenia

**Zakres funkcjonalny:**
- Integracja płatności (Stripe/PayU) **osobno per tenant** — każdy catering ma własne konto rozliczeniowe, Ty nie pośredniczysz w pieniądzach za jedzenie, tylko bierzesz swój abonament
- Kalendarz dostaw: zawieszanie/przesuwanie dni przez klienta końcowego
- Event flow: `OrderPlacedEvent` → `PaymentConfirmedEvent` → aktualizacja listy produkcyjnej dla kuchni

---

## Faza 5+ (opcjonalnie, jeśli produkt żyje)

- Logistyka i optymalizacja tras kierowców
- Raporty sprzedaży dla właściciela
- Integracja z KSeF (obowiązkowa w Polsce od kwietnia 2026 — realny problem klientów)
- Program lojalnościowy

Dodajesz te rzeczy tylko wtedy, gdy masz płacących klientów, którzy o nie proszą.

---

## Strategia biznesowa — dlaczego to może się sprzedać

Dietly to marketplace (klient trafia na Dietly.pl i wybiera catering z listy — Dietly "posiada" relację z klientem końcowym). Twój produkt to **white-label**: catering dostaje własną markę, własną domenę, własnych klientów. To realna alternatywa dla cateringów, które nie chcą być jedną z 300 pozycji na cudzej liście.

**Żeby to zadziałało:**
1. Celuj w cateringi z już rozpoznawalną marką, które chcą z niej zrobić kapitał — nie w małe firmy bez rozpoznawalności (dla nich marketplace jest wygodniejszy, bo dają darmowy ruch)
2. Konkuruj prostotą i ceną, nie liczbą funkcji — nie próbuj zbudować drugiego Dietly
3. Stały abonament miesięczny, nie prowizja od zamówień — to argument sprzedażowy sam w sobie
4. Waliduj małymi krokami (Faza 0) zanim zainwestujesz miesiące w Fazę 2–4

**Szczera ocena:** szansa na duże pieniądze — niska (rynek zdominowany przez Dietly/Żabka). Szansa na kilku–kilkunastu płacących klientów po roku pracy pobocznej — realna, jeśli pójdziesz w white-label zamiast kopiować marketplace.

---

## Powiązany dokument

Szczegółowa architektura techniczna (stack, struktura modułów Gradle, multi-tenancy w kodzie, AWS per faza, droga monolit → mikroserwisy) — osobny plik: **architektura-techniczna.md**
