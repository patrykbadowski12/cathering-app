# Słownik domeny (ubiquitous language)

Te same słowa w rozmowie, w kodzie i w bazie. Jeśli nazwa w kodzie różni się od tej tutaj, to jest sygnał do poprawki (albo do aktualizacji słownika).

| Pojęcie (PL) | W kodzie | Znaczenie |
|---|---|---|
| Tenant / catering | `Tenant`, `TenantId` | Firma cateringowa korzystająca z platformy, z własną subdomeną i wyglądem |
| Slug | `slug` | Czytelny identyfikator dla ludzi (`[a-z0-9-]`, np. `brokul`). **Nigdy** nie jest kluczem, służy do logów, URL-i i subdomen (ADR 0008) |
| Plan dietetyczny | `DietPlan` | Oferowana dieta: nazwa, kaloryczność, cena, stan (aktywna/nieaktywna) |
| Jadłospis | _do ustalenia (E2)_ | Konkretne posiłki danego planu na dany dzień |
| Zamówienie | _do ustalenia (E3)_ | Zakup planu przez klienta końcowego na określony okres |
| Klient końcowy | _do ustalenia_ | Osoba zamawiająca jedzenie u cateringu (nie nasz klient!) |
| Właściciel | _do ustalenia (E4)_ | Osoba zarządzająca tenantem w panelu, czyli nasz płacący klient |
