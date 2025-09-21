package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.client
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.RecipeSummary
import com.jacagen.jrecipe.model.TagDefinition
import com.jacagen.jrecipe.theme.useTheme
import io.ktor.client.call.*
import io.ktor.client.request.*
import js.core.JsAny
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import mui.material.Box
import mui.material.Paper
import mui.system.sx
import org.w3c.fetch.*
import org.w3c.files.File
import org.w3c.xhr.FormData
import react.FC
import react.Props
import react.use
import react.useEffect
import react.useState
import web.cssom.*

// I should probably move HTTP calls into their own helper class

val ThreeColumnLayout = FC<Props> {
    val theme = useTheme()

    var recipes by useState(emptyList<RecipeSummary>())
    var selectedRecipe by useState<Recipe?>(null)

    var tags by useState(emptyList<TagDefinition>())

    val cid = use(ConversationIdContext)!!

    useEffect(Unit) {
        val result = client.get("http://localhost:8080/recipes/summary?sortByTitle")
        recipes = result.body()
    }

    useEffect(Unit) {
        val result = client.get("http://localhost:8080/tags")
        tags = result.body()
    }

    suspend fun submitChatRequest(requestInit: RequestInit): JsAny {
        val url = // Don't hardcode
            if (selectedRecipe == null) "http://localhost:8080/chat?cid=$cid"
            else "http://localhost:8080/chat?selectedRecipe=${selectedRecipe!!.id}&cid=$cid"
        val response = window.fetch(url, requestInit).await()
        if (response.ok) return response.text().await()
        else throw Exception(response.statusText)
    }

    suspend fun retrieveRecipe(summary: RecipeSummary): Recipe {
        val url = "http://localhost:8080/recipes/${summary.id}"
        val response = window.fetch(url).await()
        if (response.ok) {
            val text = response.text().await()
            return Json.decodeFromString(text)
        }
        else throw Exception(response.statusText)
    }

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

    suspend fun onSubmitChat(input: String, file: File?) =
        submitChatRequest(createChatRequest(input, file)).toString()

    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.row
            height = 100.vh
            width = 100.pct
            overflow = Overflow.hidden
        }

        // Left column
        Paper {
            sx {
                width = 20.vw
                minWidth = 240.px
                maxWidth = 300.px
                overflowY = Overflow.scroll
                backgroundColor = Color(theme.palette.background.paper)
            }
            Navigator {
                this.recipes = recipes
                onRecipeClick = {
                    selectedRecipe = retrieveRecipe(it)
                }
                this.tags = tags
            }
        }

        // Middle column
        Box {
            sx {
                flexGrow = number(1.0)
                overflowY = Overflow.scroll
                padding = 16.px
            }
            RecipeDetail {
                recipe = selectedRecipe
            }
        }

        // Right column
        Paper {
            sx {
                width = 25.vw
                minWidth = 240.px
                //maxWidth = 300.px
                overflowY = Overflow.scroll
            }
            ChatColumn {
                onSubmitChatRequest = { msg, file -> onSubmitChat(msg, file) }
            }
        }
    }

}