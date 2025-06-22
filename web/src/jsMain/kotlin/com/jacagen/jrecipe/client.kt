package com.jacagen.jrecipe

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


val client = HttpClient {
    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
}