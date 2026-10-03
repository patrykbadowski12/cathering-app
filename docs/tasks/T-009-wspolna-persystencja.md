# T-009 — `AuditableEntity` we wspólnym miejscu

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-008

## Cel
Kod persystencji wspólny dla wielu modułów (`AuditableEntity`, w przyszłości m.in. filtr tenanta) żyje w miejscu, z którego każdy moduł może legalnie korzystać, bez łamania reguł z T-008.

## Czego się uczysz
- **Shared kernel** w DDD: co do niego należy (małe, stabilne, wspólne pojęcia), a co nie (wszystko „na wszelki wypadek”).
- Dlaczego `shared-kernel` z zależnością od JPA przestaje być „czysty” i czy to problem.

## Kryteria akceptacji
- [ ] Decyzja: `shared-kernel` czy osobny moduł (np. `shared-infrastructure`). Krótka notatka w otwartych decyzjach albo ADR 0009.
- [ ] `AuditableEntity` przeniesiona. `module-diet` i (pusty jeszcze) `module-order` mogą z niej korzystać.
- [ ] Reguły z T-008 nadal przechodzą.
- [ ] Audyt (`createdOn`, `createdBy`) nadal się zapisuje. Sprawdź testem z T-007.

## Notatki
_(Twoje notatki / pytania)_

## Review
_(uzupełnia Claude)_
