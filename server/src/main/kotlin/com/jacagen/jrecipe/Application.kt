package com.jacagen.jrecipe

import com.jacagen.jrecipe.data.dao.mongodb.recipeDao
import com.jacagen.jrecipe.llm.`interface`.recipeBot
import com.jacagen.jrecipe.model.tagsDefinitions
import com.jacagen.jrecipe.serde.InstantIso8601Serializer
import io.ktor.http.*
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.asFlow
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module).start(wait = true)
}

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Application.module() {
    install(CORS) {
        anyHost() // You can use allowHost(...) instead for stricter security
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.ContentType)
    }
    install(ContentNegotiation) {
        ignoreType<MultiPartData>()
        json(Json {
            serializersModule = SerializersModule {
                contextual(Instant::class, InstantIso8601Serializer)
            }
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        // Is there a more REST-ish way of defining some of these?
        get("/recipes/summary") {
            val sortByTitle = call.request.queryParameters.contains("sortByTitle")
            call.respond(
                if (sortByTitle) recipeDao.getSummariesSortedByTitle() else TODO()
            )
        }
        get("/recipes/{id}") {
            val id = call.parameters["id"]

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing recipe ID")
                return@get
            }

            val recipe = recipeDao.findById(id)

            if (recipe == null) {
                call.respond(HttpStatusCode.NotFound, "Recipe not found")
            } else {
                call.respond(recipe)
            }
        }
        get("/recipes") {
            val sortByTitle = call.request.queryParameters.contains("sortByTitle")
            call.respond(
                if (sortByTitle) recipeDao.getAllSortedByTitle() else recipeDao.getAll()
            )
        }
        get("/tags") {
            call.respond(tagsDefinitions)
        }
        post("/chat") {
            // Text request, current selected recipe, uploaded file
            val multipart = call.receiveMultipart()
            var message: String? = null
            var fileBytes: ByteArray? = null
            var fileName: String? = null
            val currentRecipe = call.request.queryParameters["selectedRecipe"]
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "message") {
                            message = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "file" && part.originalFileName?.isNotBlank() == true) {
                            fileName = part.originalFileName
                            fileBytes = part.streamProvider().readBytes()
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }
            if (message == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing 'message' part")
                return@post
            }

            val messagePartOne =
                if (currentRecipe == null) "" else "When answering the following, note that the the ID of the currently selected recipe is \"$currentRecipe\"\n\n\n"
            val messagePartThree =
                if (fileBytes == null) "" else "\n\n\nThe contents of the attached file are as follow: ${fileBytes.extractTextFromPdf()}"

            val response = recipeBot.chat(messagePartOne + message + messagePartThree)
            call.respondText(response)
        }
    }
}

fun ByteArray.extractTextFromPdf(): String {
    Loader.loadPDF(this).use { document ->
        return PDFTextStripper().getText(document)
    }
}