package com.jacagen.jrecipe.llm.`interface`

import com.jacagen.jrecipe.llm.model
import com.jacagen.jrecipe.llm.systemMessageText
import com.jacagen.jrecipe.llm.tool.RecipeTools
import com.jacagen.jrecipe.llm.tool.RestaurantTools
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage

interface RecipeChatBot {
    // I do not like hardcoding the `Restaurant` schema in the @SystemMessage. Or the repeated instructions.

    @UserMessage("{{it}}")
    @SystemMessage(systemMessageText)
    fun chat(message: String): String     // Later add streaming?
}

val recipeBot = AiServices.builder(RecipeChatBot::class.java).chatModel(model)
    .chatMemory(MessageWindowChatMemory.withMaxMessages(100)).tools(RecipeTools(), RestaurantTools())
    .build()