# T-002 — Projekt się kompiluje, POST/GET `/diet-plans` działa

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-001

## Cel
`./gradlew build` przechodzi. Plan utworzony przez `POST /diet-plans` jest widoczny w `GET /diet-plans`.

## Czego się uczysz
- **Porty i adaptery** (architektura heksagonalna): port `DietPlanRepository` w domenie, adapter `DietPlanRepositoryImpl` w infrastrukturze i to, kto kogo „zna”.
- Ręczna rejestracja beanów (`@Bean` w `@Configuration`), gdy klasy domenowe nie mają adnotacji Springa.
- Transakcje w Springu: jak działa `@Transactional` (proxy) i dlaczego wywołanie metody z tej samej klasy go omija.

## Kryteria akceptacji
- [ ] Kompilacja bez błędów: `findAllByTenantId` zaimplementowane, importy i nazwy w kontrolerze poprawione, `GetDietPlansUseCase` zarejestrowany jako bean.
- [ ] `tenantId` jest spójny między zapisem a odczytem: przychodzi z zewnątrz (z kontrolera), a ani domena, ani adapter nie generują własnego (ADR 0007). Na razie wystarczy stała w kontrolerze, a w T-004 zastąpi ją `TenantContext`.
- [ ] `GET /diet-plans` zwraca listę z `id`, `name`, `kcal`, `state`.
- [ ] Zapis jest transakcyjny. Wybierz miejsce dla `@Transactional` tak, żeby domena dalej nie znała Springa (ADR 0005), a wybór uzasadnij w rozmowie przy review.
- [ ] `main` przekazuje argumenty: `runApplication<...>(*args)`.
- [ ] Sprawdzone ręcznie (HTTP Client w IntelliJ / curl / Postman). Zapisz przykładowe requesty, np. w `http/diet-plans.http`.

## Wskazówki
- Mapowanie `DietPlanEntity → DietPlan` pojawia się w dwóch miejscach. Gdzie umieścić je raz?
- `findAllByTenantId`: Spring Data potrafi wygenerować zapytanie z samej nazwy metody w `DietPlanJpaRepository` (*derived query methods*).
- Zgodnie z ADR 0007 `DietPlan.create()` i use case przyjmują `tenantId` jako parametr. Możesz od razu zrobić to na `UUID`, a w T-004 zamienisz go na `TenantId`.

## Review
_(uzupełnia Claude)_
