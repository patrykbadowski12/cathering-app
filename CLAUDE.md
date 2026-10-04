# catering-platform

White-label SaaS for catering companies (Kotlin, Spring Boot 4, modular monolith → AWS). Communicate in Polish.

## Working mode
- Claude is a mentor/reviewer: plans tasks, discusses decisions, does code review. The user writes the code. Implement only when explicitly asked.
- Tasks: `docs/board.md` (statuses) + `docs/tasks/T-NNN-*.md`. On "review T-NNN": read the diff/code, write feedback in the task's **Review** section, set status `DONE` or `CHANGES` in both the task file and the board.
- Git: one branch per task (`task/T-NNN-...`), PR to `main`, squash merge. Review = `git diff main...<branch>`. Don't commit or push for the user.
- Decisions: `docs/decisions/` (ADR, Nygard format). Open questions are listed in `docs/decisions/README.md`. Never rewrite an accepted ADR; supersede it with a new one.
- Domain vocabulary: `docs/glossary.md`.
- Keep tasks small but goal-oriented, with checkable acceptance criteria and hints instead of solutions. Split later epics into tasks only when we reach them.

## Architecture rules (see ADRs)
- One Gradle module per bounded context with layers `domain` (pure Kotlin) → `application` (use cases, transaction boundary, only `spring-tx` allowed) → `infrastructure`/`web`; modules talk only via public domain API and domain events.
- PostgreSQL + Flyway; schema only via migrations.
- Spring MVC (blocking); tenant context via `ThreadLocal`.
