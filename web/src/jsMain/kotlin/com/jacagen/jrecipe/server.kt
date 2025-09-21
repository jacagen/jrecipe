package com.jacagen.jrecipe

import com.jacagen.jrecipe.model.Recipe
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.CORS
import org.w3c.fetch.DEFAULT
import org.w3c.fetch.FOLLOW
import org.w3c.fetch.RequestCache
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestMode
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.SAME_ORIGIN
import org.w3c.files.File
import org.w3c.xhr.FormData

suspend fun submitChatRequest(input: String, cid: String, selectedRecipe: Recipe?, file: File?): String {
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
    if (response.ok) return response.text().await()
    else throw Exception(response.statusText)
}