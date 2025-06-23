package com.jacagen.jrecipe.llm.`interface`

import com.jacagen.jrecipe.llm.model
import com.jacagen.jrecipe.llm.tool.RecipeTools
import com.jacagen.jrecipe.llm.tool.RestaurantTools
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage

interface RecipeChatBot {
    // I do not like hardcoding the `Restaurant` schema in the @SystemMessage. Or the repeated instructions.

    @UserMessage("{{it}}")
    @SystemMessage(
        """
        You are a helpful assistant that answers questions about recipes and restaurants.
        If you need to generate an ID for a restaurant when calling tools, please use a UUID string.
        When calling the importRestaurant tool, use the following JSON structure:
            ```
            {
              "id": "string (UUID or slug)",
              "name": "string",
              "address": "string",
              "open": {
                "hours": {
                  "MONDAY": [{ "open": "HH:MM", "close": "HH:MM" }],
                  ...
                }
              },
              "tags": ["string"],
              "source": "APPLE_NOTE" | "EVERNOTE"
            }
            ```
        """
    )
    fun chat(message: String): String     // Later add streaming?
}

val recipeBot = AiServices.builder(RecipeChatBot::class.java).chatModel(model)
    .chatMemory(MessageWindowChatMemory.withMaxMessages(100)).tools(RecipeTools(), RestaurantTools())
    .build()