# Roadmapa

Kolejność prac od dziś do produktu, który da się pokazać i sprzedać. Wizja produktu i fazy: `plan-projektu-fazy.md`. Bieżące zadania i statusy: `board.md`.

## Zasady wyboru
- **Cel:** nauka i portfolio (przygotowanie do Seniora), a sprzedaż, jeśli produkt okaże się solidny. Wybieramy rzeczy, które dają jednocześnie wartość dla właściciela cateringu i temat do nauki.
- **Głębia przed szerokością:** kilka funkcji zrobionych produkcyjnie (testy, ADR, deploy, monitoring) jest warte więcej niż wiele zrobionych w połowie.
- **Działające demo** pod publicznym adresem, z przykładowym cateringiem, to najważniejszy element portfolio.
- **Model projektujemy pod przyszłe funkcje, ale budujemy je dopiero, gdy są potrzebne** (np. przerwy w zamówieniu od razu mają autora, choć panel klienta przyjdzie później).
- **Podział pracy:** backend, infrastruktura i chmura to nauka właściciela projektu, a Claude jest mentorem i nie pisze funkcjonalnego kodu backendu. Frontend pisze Claude.

## Etap A — MVP Fazy 1 (do pierwszego deployu)

| # | Co | Dlaczego w tym miejscu | Czego się uczysz (backend) |
|---|---|---|---|
| 1 | **E1 — Diety** (ADR 0014) | wszystko inne odwołuje się do diety i wariantu | agregat z kolekcją i niezmiennikami, value object `Money`, migracja zmieniająca model, slug, wersjonowanie API |
| 2 | **T-017 — CI** (GitHub Actions) | od tej chwili każdy PR jest sprawdzany automatycznie; tanie, a zwraca się od razu | pipeline, cache Gradle i npm, Testcontainers w CI |
| 3 | **E2 — Jadłospis** | sklep przestaje używać danych przykładowych z T-016 | modelowanie w czasie (dieta → dzień → posiłki), odczyt na zakres dat, edycja kolekcji |
| 4 | **E3 — Zapytania** | pierwsza realna interakcja klienta z cateringiem; tu wraca T-009 (`AuditableEntity`) | nowy moduł, zdarzenie domenowe → mail (Mailpit lokalnie), ochrona publicznego formularza (honeypot, limit z IP) |
| 5 | **E4 — Logowanie właściciela** | panel z danymi osobowymi musi być chroniony, zanim cokolwiek pójdzie na serwer | Spring Security, sesja + CSRF, powiązanie użytkownika z tenantem, testy bezpieczeństwa |
| 6 | **E5 — Zamówienia w panelu** | właściciel wpisuje ustalone zamówienie (zwykle z zapytania); bez tego nie ma listy produkcyjnej | agregat zamówienia: pozycje z okresem i dniami tygodnia, przerwy z autorem (właściciel/klient), reguły dat |
| 7 | **E6 — Lista produkcyjna** | główna wartość dla kuchni: ile czego ugotować danego dnia | zapytania raportowe (SQL vs domena), wydajność, testy na danych granicznych (przerwy, weekendy) |
| 8 | **E7 — Deploy** | MVP pod publicznym adresem, z kontem demo | Docker, AWS (ECR, ECS/App Runner, RDS), SES, domena i TLS, sekrety, migracje przy deployu |

**Gotowe, gdy:** na publicznym adresie klient może obejrzeć diety i jadłospis i wysłać zapytanie; właściciel dostaje maila, loguje się do panelu, wpisuje zamówienie i drukuje listę produkcyjną.

## Etap B — funkcje pokazowe (produkt + tematy na rozmowę)

| # | Co | Wartość dla właściciela | Czego się uczysz |
|---|---|---|---|
| 9 | **E8 — Powiadomienia** (mail, później SMS); przypomnienie klientowi o końcu diety | nie przegapia zapytań; przypomnienie o przedłużeniu to bezpośrednio więcej sprzedaży | transactional outbox (ADR 0004, Spring Modulith), ponawianie, idempotencja, zadania cykliczne |
| 10 | **E9 — Samoobsługa klienta:** link z maila do zarządzania zamówieniem (zgłoszenie przerwy z terminem zgłoszenia, przesunięcie dni na koniec) | mniej telefonów i maili o przerwy | bezpieczne, wygasające tokeny w linkach, reguły czasowe w domenie, powiadomienie właściciela |
| 11 | **E10 — Obserwowalność** | szybkie wykrywanie problemów u klientów | logi strukturalne z tenantem, metryki, tracing (OpenTelemetry), alerty |

## Etap C — wielu klientów (Faza 2 i 3 z `plan-projektu-fazy.md`)
- Rozpoznawanie tenanta po hoście (kierunek w ADR 0007), tabela tenantów, własne domeny.
- Wygląd per tenant (`theme_config`): kolory, logo, teksty.
- Samoobsługowe zakładanie konta cateringu i abonament.

## Backlog pomysłów (bez kolejności; wybieramy według potrzeb prawdziwych właścicieli)

| Pomysł | Wartość dla właściciela | Temat do nauki |
|---|---|---|
| Raporty: przychód w miesiącu, kończące się diety, aktywni klienci | decyzje biznesowe bez Excela | zapytania analityczne, widoki/materializacja |
| Etykiety na pudełka i lista dla kierowcy (po miejscowościach) | koniec ręcznej roboty w Wordzie | generowanie PDF, druk |
| Alergeny i składniki w jadłospisie | obowiązek prawny (rozporządzenie UE 1169/2011) | modelowanie słowników, walidacja |
| Strefy dostaw (kody pocztowe / miejscowości) | mniej pustych zapytań | reguły per tenant, walidacja w formularzu |
| SMS o nowym zapytaniu | szybsza reakcja właściciela | integracja z zewnętrznym API, koszty, limity |
| Konta klientów (logowanie Google / mailem) | pełny panel klienta | OAuth2/OIDC przy wielu domenach tenantów |
| Płatności online (Przelewy24 / Stripe) | szybsze rozliczenia | webhooki, idempotencja, podpisy |
| Historia zmian (kto i kiedy zmienił cenę lub zamówienie) | rozstrzyganie sporów z klientami | Envers albo dziennik zdarzeń |
| Wybór posiłków przez klienta | funkcja premium, wyższa cena diety | złożony model jadłospisu, terminy wyboru |
