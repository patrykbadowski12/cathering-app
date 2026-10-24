# Dokumentacja projektu

| Gdzie | Co |
|---|---|
| [`board.md`](board.md) | Tablica zadań: statusy, kolejność, epiki |
| [`roadmap.md`](roadmap.md) | Kolejność prac do MVP i dalej, funkcje pokazowe, backlog pomysłów |
| [`tasks/`](tasks/) | Jeden plik = jedno zadanie (cel, kryteria, wskazówki, review) |
| [`decisions/`](decisions/) | ADR, czyli zapisane decyzje architektoniczne i otwarte pytania |
| [`glossary.md`](glossary.md) | Słownik domeny (ubiquitous language) |
| [`plan-projektu-fazy.md`](plan-projektu-fazy.md) | Wizja produktu, strategia, fazy i ich definicje „gotowe” |
| [`architektura-techniczna.md`](architektura-techniczna.md) | Architektura docelowa (przy konflikcie wygrywa ADR) |

## Workflow zadania

```
TODO → IN PROGRESS → REVIEW → DONE
                       ↓
                   CHANGES (poprawki po review) → REVIEW
```

1. Bierzesz zadanie z góry kolumny TODO w `board.md` i zmieniasz status na `IN PROGRESS`.
2. Pracujesz. Pytania i wątpliwości omawiamy w rozmowie, a decyzje Claude zapisuje jako ADR.
3. Gdy uznasz, że skończone: status `REVIEW` i piszesz do Claude'a „review T-00X”.
4. Review jest **tylko w rozmowie**. W pliku zadania Claude odhacza kryteria i ustawia `DONE` albo `CHANGES`. Pliki zadań nie mają sekcji Review (od 2026-10-13).

## Git: gałąź per zadanie

`main` zawsze się buduje i zawiera tylko zrecenzowane zmiany. Każde zadanie robisz na osobnej gałęzi:

```bash
git switch main && git pull                     # świeży main
git switch -c task/T-002-diet-path              # nowa gałąź dla zadania
# ... praca, małe commity:
git commit -m "fix(T-002): implement findAllByTenantId"
git push -u origin task/T-002-diet-path         # pierwszy push gałęzi
# → PR na GitHubie: task/T-002-... → main, status zadania: REVIEW
# → po review: merge PR (Squash and merge), usunięcie gałęzi
git switch main && git pull                     # wracasz na zaktualizowany main
```

- **Nazwa gałęzi:** `task/T-NNN-krotki-opis`
- **Commity:** `typ(T-NNN): opis` (typy: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `build`)
- **Review:** Claude czyta diff gałęzi względem `main` (`git diff main...task/T-NNN-...`), a uwagi trafiają do pliku zadania.
- **Merge:** *Squash and merge*, czyli jedno zadanie = jeden commit na `main`. Historia `main` jest czytelna, a na gałęzi możesz commitować „brudno”.

## Uruchomienie lokalne

Wymagany jest Docker (Docker Desktop z WSL2 na Windows).

```bash
docker compose up -d          # PostgreSQL z compose.yaml (dane w wolumenie "postgres-data")
./gradlew :app:bootRun        # domyślnie z profilem "local" (application-local.yaml)
docker compose down           # zatrzymanie bazy; dane zostają
docker compose down -v        # zatrzymanie + skasowanie danych

cd frontend && npm ci && npm run dev   # sklep na http://localhost:5173 (wymaga Node >= 22.12)
```

- **Front:** otwierasz `http://localhost:5173`. Vite serwuje stronę i przekazuje `/api/*` do Springa na `:8080`, więc dla przeglądarki to jedna domena (ADR 0011). Szczegóły w `frontend/README.md`.

- **IntelliJ:** w konfiguracji uruchomieniowej ustaw profil `local`. W konfiguracji typu *Spring Boot* jest pole *Active profiles*. W zwykłej konfiguracji Kotlin dodaj opcję VM `-Dspring.profiles.active=local`. Bez profilu aplikacja nie wystartuje, bo w `application.yaml` celowo nie ma danych dostępowych do bazy.
- **Kontrakt API:** `http://localhost:8080/docs` (Swagger UI), `http://localhost:8080/v3/api-docs` (JSON). Kopia w repo: `docs/api/openapi.json`, pilnowana testem `OpenApiContractTest`. Po zmianie API odśwież ją: `./gradlew :app:test --tests '*OpenApiContractTest' -Dopenapi.update=true`.
- **Testy:** `./gradlew build` nie potrzebuje `docker compose`. Testy integracyjne same stawiają Postgresa przez Testcontainers (ten sam obraz co w `compose.yaml`, losowy port), więc wymagają tylko działającego Dockera.

## Styl kodu (ktlint)

Reguły są w `.editorconfig` (czyta je i IntelliJ, i ktlint). `./gradlew build` sprawdza styl i **pada** przy naruszeniu, np. przy wildcard imporcie.

```bash
./gradlew ktlintFormat   # naprawia automatycznie wszystko, co się da (uruchom przed commitem)
./gradlew ktlintCheck    # tylko sprawdza, bez zmian w plikach
```

- Czego `ktlintFormat` nie naprawi sam (np. `import foo.*`), pokaże z numerem linii. To trzeba poprawić ręcznie.
- IntelliJ: po zmianie `.editorconfig` zrób *File → Reload All from Disk* albo zrestartuj IDE. *Reformat Code* i *Optimize Imports* używają wtedy tych samych reguł i nie tworzą wildcardów.

## Zasady zadań

- **Małe, ale z celem.** Każde zadanie kończy się czymś, co działa albo czymś, co zostało zdecydowane, a nie „połową refaktoru”.
- **Kryteria akceptacji są sprawdzalne.** Da się odpowiedzieć tak lub nie.
- **Wskazówki, nie gotowce.** Kod backendu piszesz Ty, Claude robi review i tłumaczy dlaczego. Funkcjonalnego kodu backendu Claude nie pisze nawet na prośbę; testy, konfiguracja i narzędzia są OK.
- Zadania z późniejszych epików rozpisujemy dopiero, gdy do nich dojdziemy, bo wtedy wiemy więcej.

## Zasady ADR

- Nowa decyzja to nowy plik `decisions/NNNN-tytul.md`. Starych plików nie edytujemy merytorycznie.
- Zmiana zdania to nowy ADR ze statusem `Accepted`, a stary dostaje `Superseded by NNNN`.
- Pytania jeszcze bez odpowiedzi trafiają do sekcji „Otwarte decyzje” w `decisions/README.md`.
