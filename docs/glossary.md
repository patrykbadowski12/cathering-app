# Słownik domeny (ubiquitous language)

Te same słowa w rozmowie, w kodzie i w bazie. Jeśli nazwa w kodzie różni się od tej tutaj, to jest sygnał do poprawki (albo do aktualizacji słownika).

| Pojęcie (PL) | W kodzie | Znaczenie |
|---|---|---|
| Tenant / catering | `Tenant`, `TenantId` | Firma cateringowa korzystająca z platformy, z własną subdomeną i wyglądem |
| Slug | `slug` | Czytelny identyfikator dla ludzi (`[a-z0-9-]`, np. `brokul`). **Nigdy** nie jest kluczem, służy do logów, URL-i i subdomen (ADR 0008) |
| Dieta | `Diet` (dziś jeszcze `DietPlan`, zmiana w E1) | Oferowana dieta tenanta (np. Keto): nazwa, opis, slug, stan (aktywna/nieaktywna) i co najmniej jeden wariant (ADR 0014) |
| Wariant diety | `DietVariant` | Kaloryczność diety z ceną brutto za dzień, np. Keto 1500 kcal za 60 zł. Wszystkie warianty diety mają ten sam jadłospis |
| Cena | `Money` | Kwota brutto w groszach, w PLN. Płatność poza systemem (przelew po ustaleniach z właścicielem) |
| Jadłospis | _do ustalenia (E2)_ | Posiłki danej diety na dany dzień, wspólne dla wszystkich jej wariantów; tworzy go właściciel |
| Zapytanie | _do ustalenia_ | Wiadomość klienta końcowego ze sklepu: wybrany wariant diety, kontakt i preferencje. Trafia mailem do właściciela i na listę w panelu |
| Zamówienie | _do ustalenia_ | Ustalona z klientem dostawa diety, wprowadzana przez właściciela w panelu (zwykle na podstawie zapytania) |
| Klient końcowy | _do ustalenia_ | Osoba zamawiająca jedzenie u cateringu (nie nasz klient!) |
| Właściciel | _do ustalenia (E4)_ | Osoba zarządzająca tenantem w panelu, czyli nasz płacący klient |
| Sklep | `/`, `/api/v1/shop` | Publiczna część strony tenanta dla klienta końcowego: diety, jadłospis, zgłoszenie zamówienia (ADR 0012) |
| Panel | `/admin`, `/api/v1/admin` | Część strony tenanta dla zalogowanego właściciela: zamówienia, lista produkcyjna, diety, jadłospisy (ADR 0012) |
| Lista produkcyjna | _do ustalenia (E6)_ | Zestawienie na dany dzień: ile jakich posiłków przygotować, wyliczone z zamówień i jadłospisu |
