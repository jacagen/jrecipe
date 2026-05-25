# AGENTS.md

## Repository overview

- `server`: Kotlin/JVM Ktor backend.
- `web`: Kotlin/JS React frontend.
- `shared`: Kotlin multiplatform shared models and serializers.
- `composeApp`: abandoned Compose-for-Web experiment. Do not extend it unless the task is explicitly about that module.

This repo is a Gradle multi-project build rooted at `jrecipe`.

## Working norms

- Keep changes scoped to the module that owns the behavior.
- Prefer existing Kotlin patterns over introducing new abstractions.
- Preserve the current split: HTTP and persistence logic in `server`, UI logic in `web`, shared DTOs/models in `shared`.
- The frontend currently talks directly to `http://localhost:8080`; do not silently change ports or transport assumptions without updating both sides.
- Whenever you author content yourself, including code, documentation, plans, or work items, add a short attribution note near the top crediting Codex as the writer.

## Common commands

- Run backend: `./gradlew :server:run`
- Run backend tests: `./gradlew :server:test`
- Run shared tests: `./gradlew :shared:jvmTest`
- Run web tests: `./gradlew :web:jsTest`
- Build everything: `./gradlew build`

## Codebase notes

- Backend entrypoint is `server/src/main/kotlin/com/jacagen/jrecipe/Application.kt`.
- Server port is defined in `shared/src/commonMain/kotlin/com/jacagen/jrecipe/Constants.kt` and is currently `8080`.
- Web API calls are hard-coded in `web/src/jsMain/kotlin/com/jacagen/jrecipe/server.kt`.
- MongoDB access is initialized in `server/src/main/kotlin/com/jacagen/jrecipe/data/dao/mongodb/mongodb.kt`.
- LLM integration lives under `server/src/main/kotlin/com/jacagen/jrecipe/llm`.

## Testing expectations

- For backend API or serialization changes, run `:server:test`.
- For shared model/serializer changes, run `:shared:jvmTest` and any affected module tests.
- For frontend behavior changes, run `:web:jsTest` when practical.
- If tests cannot be run, state that explicitly in the handoff.

## Avoid

- Do not add new work to `composeApp` unless requested.
- Do not move shared models into `server` or `web`.
- Do not introduce alternate server ports or base URLs without updating the whole flow deliberately.
