package com.jacagen.jrecipe

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
                listOf(
                    Recipe(
                        Uuid.random(), "Chocolate Cake",
                        source = RecipeSource.EVERNOTE,
                        "ChatGPT",
                        "https://chatgpt.com/share/6834c0c6-21f8-8003-8e84-3892d08d7c77",
                        "Flour, eggs, sugar...",
                        createdInSource = Clock.System.now(),
                        updatedInSource = Clock.System.now(),
                        tags = emptySet(),
                    ),
                    Recipe(
                        Uuid.random(), "Pasta",
                        source = RecipeSource.EVERNOTE,
                        "ChatGPT",
                        "https://chatgpt.com/share/6834c0c6-21f8-8003-8e84-3892d08d7c77",
                        "Boil water, add pasta...",
                        createdInSource = Clock.System.now(),
                        updatedInSource = Clock.System.now(),
                        tags = emptySet(),
                    ),
                )
            )
        }
    }
}