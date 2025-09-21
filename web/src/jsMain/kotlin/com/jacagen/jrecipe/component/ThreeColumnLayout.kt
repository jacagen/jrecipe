package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.client
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.RecipeSummary
import com.jacagen.jrecipe.model.TagDefinition
import com.jacagen.jrecipe.submitChatRequest
import com.jacagen.jrecipe.theme.useTheme
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import mui.material.Box
import mui.material.Paper
import mui.system.sx
import org.w3c.files.File
import react.*
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

    suspend fun retrieveRecipe(summary: RecipeSummary): Recipe {
        val url = "http://localhost:8080/recipes/${summary.id}"
        val response = window.fetch(url).await()
        if (response.ok) {
            val text = response.text().await()
            return Json.decodeFromString(text)
        } else throw Exception(response.statusText)
    }

    suspend fun onSubmitChat(input: String, file: File?) = submitChatRequest(input, cid, selectedRecipe, file)

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