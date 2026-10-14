# catering-platform

White-label SaaS for catering companies (Kotlin, Spring Boot 4, modular monolith → AWS). Communicate in Polish.

## Working mode
- Split of ownership (column "Kto" on the board):
  - **Backend, infrastructure, cloud:** the user writes the code. Claude is a mentor/reviewer: plans tasks, discusses decisions, does code review, and implements only when explicitly asked.
  - **Frontend (`frontend/`, ADR 0011):** Claude writes it. The user accepts the result but is not interested in frontend details. Code must stay conventional so another developer can take it over.
- Code Claude writes: no explanatory comments/KDoc (explanations go in chat or the task Review); `TODO(T-NNN)` for deliberate temporary states is fine.
- Tasks: `docs/board.md` (statuses) + `docs/tasks/T-NNN-*.md`. On "review T-NNN": read the diff/code and give the review **in chat only**; in the task file just tick acceptance criteria and set status `DONE` or `CHANGES` (task file and board). Task files have no Review section (since 2026-10-13).
- Git: one branch per task (`task/T-NNN-...`), PR to `main`, squash merge. Review = `git diff main...<branch>`. Don't commit or push for the user.
- Decisions: `docs/decisions/` (ADR, Nygard format). Open questions are listed in `docs/decisions/README.md`. Never rewrite an accepted ADR; supersede it with a new one.
- Domain vocabulary: `docs/glossary.md`.
- Keep tasks small but goal-oriented, with checkable acceptance criteria and hints instead of solutions. Split later epics into tasks only when we reach them.

## Architecture rules (see ADRs)
- One Gradle module per bounded context with layers `domain` (pure Kotlin) → `application` (use cases, transaction boundary, only `spring-tx` allowed) → `infrastructure`/`web`; modules talk only via public domain API and domain events.
- PostgreSQL + Flyway; schema only via migrations.
- Spring MVC (blocking); tenant context via `ThreadLocal`.
- Backend exposes REST only (`/api/v1/...`); OpenAPI is the contract, the TypeScript client is generated from it. Frontend: React + TypeScript (Vite) in `frontend/`, same origin as the API (ADR 0011).
