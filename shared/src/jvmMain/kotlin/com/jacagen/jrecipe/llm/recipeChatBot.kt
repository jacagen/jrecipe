package com.jacagen.jrecipe.llm

import com.jacagen.jrecipe.service.EvernoteToLlmConverter
import com.jacagen.jrecipe.service.RecipeChatBot
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.service.AiServices

val recipeBot = AiServices.builder(RecipeChatBot::class.java)
    .chatModel(model)
    .build()

