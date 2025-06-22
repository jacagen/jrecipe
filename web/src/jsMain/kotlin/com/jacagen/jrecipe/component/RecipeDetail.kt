package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.model.Ingredient
import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.Tag
import js.objects.jso
import mui.icons.material.Kitchen
import mui.icons.material.Label
import mui.icons.material.LocalDining
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import web.cssom.*
import web.cssom.PropertyName.Companion.alignItems

external interface RecipeDetailProps : Props {
    var recipe: Recipe?
}

val RecipeDetail = FC<RecipeDetailProps> { props ->
    val recipe = props.recipe

    Box {
        sx = jso {
            padding = 16.px
            flexGrow = number(3.0)
            display = Display.flex
            flexDirection = FlexDirection.column
        }

        if (recipe != null) {
            RecipeTitle { title = recipe.title }
            RecipeTagRow { tags = recipe.tags }
            recipe.yield?.let { Yield { yield = it } }
            recipe.notes?.let { Notes { notes = it } }
            recipe.ingredients?.let { Ingredients { ingredients = it } }
            recipe.steps?.let { Steps { steps = it } }
        }
    }
}

external interface IngredientsProps : Props {
    var ingredients: List<Ingredient>
}

val Ingredients = FC<IngredientsProps> { props ->
    val ingredients = props.ingredients

    if (ingredients.isNotEmpty()) {
        Box {
            sx {
                marginBottom = 16.px
            }
            RecipeHeader { title = "Ingredients" }
//        ingredients.forEach { ingredient ->
//            Stack {
//                direction = responsive(StackDirection.row)
//                //spacing = responsive(1)
//                sx {
//                    alignItems = AlignItems.center
//                }
//                ListItemIcon {
//                    sx = jso { minWidth = 20.px }
//                    Kitchen {
//                        sx = jso { fontSize = 20.px }
//                    }
//                }
//                ReactMarkdown {
//                    children = ingredient.format()
//                }
//            }
//        }

            Stack {
                sx {
                    marginTop = 4.px
                }
                direction = responsive(StackDirection.column)
                ingredients.forEach { ingredient ->
                    Stack {
                        direction = responsive(StackDirection.row)
                        spacing = responsive(1)
                        ListItemIcon {
                            sx = jso { minWidth = 20.px }
                            Kitchen {
                                sx = jso { fontSize = 20.px }
                            }
                        }
                        ReactMarkdown {
                            children = ingredient.format()
                        }
                    }
                }
            }
        }
    }
}

private fun Ingredient.format() = StringBuilder().apply {
    if (amount != null) append(amount)
    if (unit != null) append(" ").append(unit)
    append(" ").append(ingredient)
    if (note != null) append(", ").append(note)
}.trimStart().toString()

external interface RecipeHeaderProps : Props {
    var title: String
}

external interface RecipeTitleProps : Props {
    var title: String
}

var RecipeTitle = FC<RecipeTitleProps> { props ->
    val title = props.title
    Box {
        sx = jso { display = web.cssom.Display.flex; alignItems = web.cssom.AlignItems.center }

        ListItemIcon {
            sx = jso { minWidth = 40.px }
            LocalDining()
        }

        Typography {
            variant = TypographyVariant.h4
            +title
        }
    }
}

var RecipeHeader = FC<RecipeHeaderProps> { props ->
    val title = props.title
    Typography {
        variant = TypographyVariant.h6
        +title
    }
}

external interface RecipeTagRowProps : Props {
    var tags: Set<Tag>
}

val RecipeTagRow = FC<RecipeTagRowProps> { props ->
    if (props.tags.isNotEmpty()) {
        Box {
            sx {
                display = Display.flex
                flexWrap = FlexWrap.wrap
                gap = 8.px
                width = 100.pct
                marginTop = 4.px
                marginBottom = 16.px
            }

            props.tags.forEach { tag ->
                Chip {
                    label = ReactNode(tag)
                    icon = Label.create() {
                        sx = jso { fontSize = 16.px }
                    }
                    sx {
                        paddingInline = 8.px
                        paddingBlock = 4.px
                        fontSize = 13.px
                    }
                }
            }
        }
    }
}

external interface YieldProps : Props {
    var yield: String
}

val Yield = FC<YieldProps> { props ->
    val yield = props.yield
    RecipeHeader { title = "Yield" }
    ReactMarkdown {
        children = yield
    }
}

external interface NotesProps : Props {
    var notes: String
}

val Notes = FC<NotesProps> { props ->
    val notes = props.notes
    RecipeHeader { title = "Notes" }
    ReactMarkdown {
        children = notes
    }
}

external interface StepsProps : Props {
    var steps: List<String>
}

val Steps = FC<StepsProps> { props ->
    val steps = props.steps
    if (steps.isNotEmpty()) {
        RecipeHeader { title = "Steps" }
        Stack {
            direction = responsive(StackDirection.column)
            steps.withIndex().forEach { (index, step) ->
                Stack {
                    direction = responsive(StackDirection.row)
                    sx = jso {
                        alignItems = AlignItems.flexStart
                        gap = 8.px // optional: space between number and text
                    }

                    ReactMarkdown {
                        children = "${index + 1}."
                    }
                    ReactMarkdown {
                        children = step
                    }
                }
            }
        }
    }
}

