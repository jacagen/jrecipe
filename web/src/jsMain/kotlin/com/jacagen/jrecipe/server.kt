package com.jacagen.jrecipe

import com.jacagen.jrecipe.model.ChatResponse
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.RecipeSummary
import com.jacagen.jrecipe.model.TagDefinition
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.w3c.fetch.*
import org.w3c.files.File
import org.w3c.xhr.FormData

internal suspend fun retrieveRecipeSummaries(): List<RecipeSummary> =
    client.get("http://localhost:8080/recipes/summary?sortByTitle").body()

internal suspend fun retrieveTags(): List<TagDefinition> = client.get("http://localhost:8080/tags").body()

internal suspend fun retrieveRecipe(summary: RecipeSummary): Recipe {
    val url = "http://localhost:8080/recipes/${summary.id}"
    val response = window.fetch(url).await()
    if (response.ok) {
        val text = response.text().await()
        return Json.decodeFromString(text)
    } else throw Exception(response.statusText)
}

internal suspend fun submitChatRequest(input: String, cid: String, selectedRecipe: Recipe?, file: File?): ChatResponse {
    fun createChatRequest(input: String, file: File? = null): RequestInit {
        val formData = FormData()
        formData.append("message", input)
        if (file != null) {
            formData.append("file", file, file.name)
        }
        return RequestInit(
            method = "POST", body = formData
        ).apply {
            referrer = ""
            referrerPolicy = "no-referrer"
            mode = RequestMode.CORS
            credentials = RequestCredentials.SAME_ORIGIN
            cache = RequestCache.DEFAULT
            redirect = RequestRedirect.FOLLOW
            integrity = ""
            keepalive = false
        }
    }

    val url = // Don't hardcode
        if (selectedRecipe == null) "http://localhost:8080/chat?cid=$cid"
        else "http://localhost:8080/chat?selectedRecipe=${selectedRecipe.id}&cid=$cid"
    val requestInit = createChatRequest(input, file)
    val response = window.fetch(url, requestInit).await()
    if (response.ok) {
        val text = response.text().await()
        return Json.decodeFromString(text)
    }
    else throw Exception(response.statusText)
}