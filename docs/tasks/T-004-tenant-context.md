# T-004 — Decyzja i implementacja: skąd bierze się `tenantId`

**Status:** TODO · **Etap:** 0 · **Zależy od:** T-002

## Cel
Jest jedno, jawne źródło `tenantId` w aplikacji. Decyzja jest zapisana jako ADR 0007.

## Czego się uczysz
- Różnica między danymi biznesowymi (nazwa diety) a kontekstem wykonania (kto, dla jakiego tenanta).
- `ThreadLocal`, `HandlerInterceptor`/`Filter` i cykl życia requestu w Spring MVC.
- **YAGNI vs „tanie teraz, drogie później”**: jak rozróżnić, co warto przygotować z wyprzedzeniem.

## Część 1: dyskusja (najpierw rozmowa, potem kod)
Opcje:
1. Bez `tenant_id` do Fazy 2 (czyste YAGNI).
2. Kolumna `tenant_id` jest od teraz, a wartość pochodzi ze stałej w `application.yaml`.
3. `TenantContext` w `shared-kernel` już teraz. W Fazie 1 interceptor zawsze ustawia ten sam, domyślny tenant. W Fazie 2 podmieniamy tylko sposób rozpoznawania (subdomena).

Przemyśl: ile kosztuje dodanie `tenant_id` do istniejących danych później? Co zyskujesz i tracisz w każdej opcji?

## Kryteria akceptacji
- [ ] ADR `decisions/0007-zrodlo-tenant-id.md` zapisany (możesz go napisać sam, a ja zrobię review).
- [ ] Implementacja zgodna z ADR. Domena nie generuje `tenantId` z powietrza.
- [ ] Jeśli `ThreadLocal`: kontekst jest **zawsze** czyszczony po requeście, także gdy poleci wyjątek.
- [ ] Typ `TenantId` (zamiast gołego `UUID`) w `shared-kernel`, jeśli wybierzesz opcję 2 lub 3.
- [ ] Test potwierdzający, że kontekst nie „przecieka” między requestami.

## Notatki
_(Twoje notatki / pytania)_

## Review
_(uzupełnia Claude)_
