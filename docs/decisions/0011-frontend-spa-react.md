# 0011 — Frontend: SPA w React + TypeScript, ten sam origin co API

**Status:** Accepted · **Data:** 2026-10-11 · **Zastępuje:** Thymeleaf (SSR) z `architektura-techniczna.md`

## Kontekst
Potrzebujemy dwóch interfejsów:
- **sklep** dla klienta końcowego: jadłospis, wybór diety, zgłoszenie zamówienia,
- **panel właściciela:** zamówienia, lista produkcyjna (ile jakich posiłków na dany dzień), diety i jadłospisy.

Właściciel projektu odpowiada za backend, infrastrukturę i chmurę; frontend pisze Claude. Kod frontu musi być konwencjonalny, żeby inna osoba mogła go przejąć bez wdrażania się w nietypowe rozwiązania. Thymeleaf odrzucony, bo przy interaktywnym panelu i tak skończyłby się przepisaniem.

## Decyzja
- **Stack:** React + TypeScript (strict), budowany przez Vite. Konwencjonalny zestaw bibliotek:
  - routing: React Router,
  - stan serwerowy: TanStack Query,
  - formularze: React Hook Form + Zod,
  - UI: Tailwind CSS + shadcn/ui,
  - testy: Vitest + Testing Library,
  - jakość: ESLint + Prettier.
- **Jedna aplikacja, dwie strefy:** sklep (`/`) i panel (`/admin`, ładowany leniwie).
- **Repozytorium:** katalog `frontend/` w tym samym repo. Funkcja = jeden PR z backendem i frontem.
- **Ten sam origin co API:**
  - lokalnie Vite przekierowuje `/api/*` na Spring,
  - na produkcji zbudowane pliki są serwowane spod tej samej domeny (Spring albo CloudFront przed nim).

  Dzięki temu:
  - `TenantFilter` (ADR 0007) działa bez zmian, bo przeglądarka wysyła `Host` tenanta,
  - logowanie właściciela to sesja Spring Security + ciasteczko, bez JWT i CORS.
- **Kontrakt API:** OpenAPI generowane przez backend jest źródłem prawdy. Typowany klient TypeScript jest z niego **generowany**, a nie pisany ręcznie, więc zmiana kontraktu psuje kompilację frontu, a nie działanie u klienta.
- **Błędy API:** `application/problem+json` (RFC 9457, `ProblemDetail` w Springu), z błędami pól dla formularzy.
- **SEO sklepu odłożone:** w Fazie 1 sklep jest SPA. Gdy SEO lub podglądy linków zaczną mieć znaczenie (wielu tenantów, ruch z wyszukiwarek), sklep przechodzi na Next.js. To migracja (komponenty, klient API i logika zostają), a nie przepisanie.

## Konsekwencje
- Backend wystawia wyłącznie REST API (`/api/v1/...`). Nie ma szablonów po stronie serwera.
- Każda funkcja to pionowy kawałek: kontrakt API (backend) + ekran (frontend).
- Dochodzi Node.js do lokalnego developmentu i do builda frontu. Docelowy deploy (S3 + CloudFront vs pliki w Springu) rozstrzygamy przy CI/CD.
- Next.js dla sklepu oznaczałby dodatkowy runtime Node na produkcji, dlatego jest odłożony do momentu realnej potrzeby (zasada jak w ADR 0001).
