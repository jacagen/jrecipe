package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.client
import com.jacagen.jrecipe.model.Recipe
import io.ktor.client.call.body
import io.ktor.client.request.get
import mui.material.Box
import mui.material.Paper
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useState
import web.cssom.Border
import web.cssom.Display
import web.cssom.Flex
import web.cssom.FlexDirection
import web.cssom.LineStyle
import web.cssom.NamedColor
import web.cssom.Overflow
import web.cssom.number
import web.cssom.pct
import web.cssom.px
import web.cssom.vh
import web.cssom.vw

val ThreeColumnLayout = FC<Props> {
    var recipes by useState(emptyList<Recipe>())
    var selectedRecipe by useState<Recipe?>(null)

    useEffect(Unit) {
        val result = client.get("http://localhost:8080/recipes?sortByTitle")
        recipes = result.body()
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
            }
            Navigator {
                this.recipes = recipes
                onRecipeClick = { selectedRecipe = it }
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
            +"Chat Column"
        }
    }
}