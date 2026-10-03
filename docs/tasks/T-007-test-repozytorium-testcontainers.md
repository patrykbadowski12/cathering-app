# T-007 — Test integracyjny repozytorium z Testcontainers

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-006

## Cel
Adapter `DietPlanRepositoryImpl` jest przetestowany na prawdziwym PostgreSQL, z migracjami Flyway uruchamianymi w teście.

## Czego się uczysz
- Piramida testów: co testujemy jednostkowo (T-003), a co integracyjnie.
- Testcontainers: kontener bazy na czas testów, `@ServiceConnection` w Spring Boot.
- Test slice `@DataJpaTest` vs pełny `@SpringBootTest`: koszt i zakres.

## Kryteria akceptacji
- [ ] Test zapisuje `DietPlan` przez port `DietPlanRepository` i odczytuje go z powrotem (round-trip mapowania).
- [ ] Test `findAllByTenantId` potwierdza, że plany innego tenanta nie wracają.
- [ ] Test używa Flyway (bez `ddl-auto: create`).
- [ ] `CateringPlatformApplicationTests.contextLoads` też działa na Testcontainers.
- [ ] `./gradlew test` przechodzi przy włączonym Dockerze.

## Wskazówki
- Kontener startuje kilka sekund. Sprawdź, jak go współdzielić między klasami testowymi zamiast stawiać nowy dla każdej.

## Notatki
_(Twoje notatki / pytania)_

## Review
_(uzupełnia Claude)_
