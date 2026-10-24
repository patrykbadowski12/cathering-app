# T-021 — API sklepu: aktywne diety i szczegóły po slugu

**Status:** TODO · **Etap:** A / E1 · **Kto:** Ty · **Zależy od:** T-019 · **ADR:** 0008, 0012, 0014

## Cel
Sklep przez `/api/v1/shop/diets` pokazuje klientowi tylko to, co właściciel włączył, a szczegóły diety są dostępne pod czytelnym adresem ze slugiem.

## Czego się uczysz
- **Różne DTO dla różnych odbiorców** (ADR 0012): sklep nie widzi nieaktywnych diet ani wariantów ani pól wewnętrznych.
- **Slug w URL-u** (ADR 0008): identyfikator dla ludzi i wyszukiwarek, ale klucz w bazie to dalej UUID.
- **Pole wyliczane w odpowiedzi** („cena od”): liczone w domenie, w use case'ie czy w mapperze DTO? Gdzie powinna leżeć taka logika?

## Kryteria akceptacji
- [ ] Lista: tylko aktywne diety, w każdej tylko aktywne warianty posortowane po kaloryczności, plus najniższa cena („od X zł”).
- [ ] Szczegóły po slugu: nazwa, opis, aktywne warianty z ceną. Nieaktywna lub nieistniejąca dieta → `404` jako `ProblemDetail`.
- [ ] Odpowiedzi nie zawierają `tenantId` ani stanu (w sklepie wszystko jest aktywne).
- [ ] Stare `/api/v1/shop/diet-plans` dalej działa (usuwamy je w T-024).
- [ ] OpenAPI odświeżone; testy slice + integracyjny (izolacja tenantów, ukrywanie nieaktywnych).

## Wskazówki
- Filtrowanie aktywnych: w zapytaniu do bazy czy w pamięci po załadowaniu agregatu? Przy kilkunastu dietach na tenanta oba działają. Uzasadnij wybór.
- Jadłospis w sklepie (T-016) wybiera dietę po id w adresie (`?dieta=<id>`). Po tym zadaniu przejdzie na slug; zrobię to w T-022.
