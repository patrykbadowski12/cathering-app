# T-015 — Typowany klient API + pierwszy ekran end-to-end

**Status:** DONE · **Etap:** 0 · **Kto:** Claude · **Zależy od:** T-012, T-013 · **ADR:** 0011

## Cel
Front rozmawia z backendem przez klienta **wygenerowanego** z OpenAPI. Pierwszy realny ekran (lista diet w sklepie i prosty formularz dodania diety w panelu) działa na prawdziwym API.

## Zakres
- Generowanie typów i klienta z `/v3/api-docs` (np. `openapi-typescript` + `openapi-fetch`), skrypt `npm run api:generate`, wygenerowany kod w repo.
- Sklep: lista diet (nazwa, kcal), stany ładowania, pustej listy i błędu.
- Panel: formularz dodania diety (React Hook Form + Zod), błędy z `ProblemDetail` (T-014) pokazane przy polach.

## Kryteria akceptacji
- [x] Zmiana pola w DTO na backendzie → po `api:generate` front się nie kompiluje, dopóki nie zostanie dostosowany.
- [x] Lista diet pokazuje dane z backendu (tenant z filtra, ten sam origin).
- [x] Dodanie diety w panelu → pojawia się na liście; błąd walidacji pokazany przy polu.
- [x] Testy komponentów dla listy i formularza.

