package com.jacagen.jrecipe.llm

import com.jacagen.jrecipe.service.RecipeChatBot
import com.jacagen.jrecipe.tool.RecipeHelper
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.service.AiServices

val recipeBot = AiServices.builder(RecipeChatBot::class.java)
    .chatModel(model)
    .chatMemory(MessageWindowChatMemory.withMaxMessages(100))
    .tools(RecipeHelper())
    .build()

