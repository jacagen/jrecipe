package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.client
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.TagDefinition
import com.jacagen.jrecipe.theme.useTheme
import io.ktor.client.call.*
import io.ktor.client.request.*
import mui.icons.material.Chat
import mui.material.Box
import mui.material.Paper
import mui.system.sx
import react.FC
import react.Props
import react.useEffect
import react.useState
import web.cssom.*

val ThreeColumnLayout = FC<Props> {
    val theme = useTheme()

    var recipes by useState(emptyList<Recipe>())
    var selectedRecipe by useState<Recipe?>(null)

    var tags by useState(emptyList<TagDefinition>())

    useEffect(Unit) {
        val result = client.get("http://localhost:8080/recipes?sortByTitle")
        recipes = result.body()
    }

    useEffect(Unit) {
        val result = client.get("http://localhost:8080/tags")
        tags = result.body()
    }

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
                onRecipeClick = { selectedRecipe = it }
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
                width = 20.vw
                minWidth = 240.px
                maxWidth = 300.px
                overflowY = Overflow.scroll
            }
            ChatColumn()
        }
    }
}