package com.jacagen.jrecipe.tool

import com.jacagen.jrecipe.model.Restaurant
import dev.langchain4j.agent.tool.Tool
import kotlinx.coroutines.runBlocking

class RestaurantTools {
    @Tool
    fun importRestaurant(restaurant: Restaurant) = runBlocking {
        println("Restaurant is $restaurant")
    }
}