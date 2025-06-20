package com.jacagen.jrecipe.llm.tool

import com.jacagen.jrecipe.llm.serde.RestaurantDTO
import dev.langchain4j.agent.tool.Tool
import kotlinx.coroutines.runBlocking

@Suppress("unused")
class RestaurantTools {
    @Tool
    fun importRestaurant(restaurant: RestaurantDTO) = runBlocking {
        println("Restaurant is $restaurant")
    }
}