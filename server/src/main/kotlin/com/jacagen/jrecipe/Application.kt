package com.jacagen.jrecipe

import com.jacagen.jrecipe.dao.mongodb.recipeDao
import com.jacagen.jrecipe.model.InstantIso8601Serializer
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Application.module() {
    install(CORS) {
        anyHost() // You can use allowHost(...) instead for stricter security
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.ContentType)
    }
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                contextual(Instant::class, InstantIso8601Serializer)
            }
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        get("/recipes") {
            val sortByTitle = call.request.queryParameters.contains("sortByTitle")
            call.respond(
                if (sortByTitle) recipeDao.getAllSortedByTitle() else recipeDao.getAll()
            )
        }
    }
}