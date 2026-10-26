# JRecipe embedding-repair checkpoint

Author credit: Codex.

## Current task

Make targeted recipe-embedding generation runnable through Gradle.

## Findings

- Local MongoDB database `jcmenu` is running; the JRecipe HTTP server is not currently listening on port 8080.
- Two recipes had absent or null embeddings: `Classic Spaghetti and Meatballs` (`bab3cd82-5899-4a29-bab3-7283f9365cb8`) and `Tamago Kake Gohan (Egg Over Rice)` (`892fc23b-9cdc-4222-9d37-93b55c8b2518`).
- The existing uncommitted loader change adds `--create-embeddings-for-ids`, which is preferable to rebuilding all embeddings.
- An uncommitted IDE run configuration contains an OpenAI API key. Rotate it and do not commit that configuration change.

## Change made

- Added `:server:embedRecipes`, a `JavaExec` task running `com.jacagen.jrecipe.importer.LoaderKt`.

## Validation

- `./gradlew :server:tasks --group application --console=plain` completed successfully and listed `embedRecipes`.
- 2026-10-25: Ran the targeted task using the locally configured API key. MongoDB access succeeded, but OpenAI returned `credit_balance_exhausted`; neither recipe was updated.

## Next step

Add OpenAI API credits (and rotate the exposed key), then run `OPENAI_API_KEY=... ./gradlew :server:embedRecipes --args="--create-embeddings-for-ids=bab3cd82-5899-4a29-bab3-7283f9365cb8,892fc23b-9cdc-4222-9d37-93b55c8b2518"`, then verify the two records have embeddings.
