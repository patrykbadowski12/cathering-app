# T-009 — `AuditableEntity` we wspólnym miejscu

**Status:** TODO · **Etap:** 0 → start E3 · **Zależy od:** T-008, pierwsza encja w `module-order`

## Odłożone do startu E3 (2026-10-18)
Dziś z `AuditableEntity` korzysta tylko `module-diet`, a `module-order` jest pusty. Wspólne miejsce dla kodu technicznego wymaga decyzji (nowy moduł albo kopia w module, wyjątek w ArchUnit, ADR), a płacilibyśmy za nią przed pojawieniem się drugiego użytkownika (YAGNI). Zmiany audytu są rzadkie, więc koszt ewentualnej kopii jest mały.

Wracamy do tego przy pierwszej encji w `module-order`. Wtedy będzie widać, czy dochodzi więcej wspólnego kodu technicznego (np. filtr tenanta w Hibernate) i czy osobny moduł ma sens.

Same kolumny audytu zostają: `createdOn` przyda się przy zamówieniach (E3, E5), a `createdBy`/`modifiedBy` nabiorą sensu z logowaniem (E4). Kolumny pokazują tylko ostatnią zmianę; jeśli będzie potrzebna historia (np. zmian cen), to osobna decyzja.

## Cel
Kod persystencji wspólny dla wielu modułów (`AuditableEntity`, w przyszłości m.in. filtr tenanta) żyje w miejscu, z którego każdy moduł może legalnie korzystać, bez łamania reguł z T-008.

## Czego się uczysz
- **Shared kernel** w DDD: co do niego należy (małe, stabilne, wspólne pojęcia), a co nie (wszystko „na wszelki wypadek”).
- Dlaczego `shared-kernel` z zależnością od JPA przestaje być „czysty” i czy to problem.

## Kryteria akceptacji
- [ ] Decyzja: `shared-kernel` czy osobny moduł (np. `shared-infrastructure`). Zapisana jako ADR (kolejny wolny numer).
- [ ] `AuditableEntity` przeniesiona. `module-diet` i (pusty jeszcze) `module-order` mogą z niej korzystać.
- [ ] Reguły z T-008 nadal przechodzą.
- [ ] Audyt (`createdOn`, `createdBy`) nadal się zapisuje. Sprawdź testem z T-007.

