package com.jacagen.jrecipe.llm

import java.nio.file.Files
import java.nio.file.Path

// Authorship note: Codex added local .env loading while retaining environment-variable precedence.
internal val openAiApiKey: String = System.getenv("OPENAI_API_KEY")
    ?: loadOpenAiApiKeyFromDotEnv()
    ?: error("OPENAI_API_KEY must be set in the environment or .env before running JRecipe")

private fun loadOpenAiApiKeyFromDotEnv(): String? {
    val envFile = generateSequence(Path.of("").toAbsolutePath()) { it.parent }
        .map { it.resolve(".env") }
        .firstOrNull(Files::isRegularFile)
        ?: return null

    return Files.readAllLines(envFile)
        .firstNotNullOfOrNull { line ->
            val (name, value) = line.split("=", limit = 2).let { parts ->
                if (parts.size == 2) parts else return@firstNotNullOfOrNull null
            }
            if (name.trim() == "OPENAI_API_KEY") value.trim().removeSurrounding("\"") else null
        }
}
