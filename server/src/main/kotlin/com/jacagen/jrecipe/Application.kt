package com.jacagen.jrecipe

import com.jacagen.jrecipe.dao.mongodb.recipeDao
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.RecipeSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Application.module() {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        get("/recipes") {
            call.respond(
                recipeDao.getAll()
            )
        }
    }
}