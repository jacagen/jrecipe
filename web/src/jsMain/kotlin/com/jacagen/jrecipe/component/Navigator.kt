package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.model.Recipe
import js.objects.jso
import mui.icons.material.ExpandLess
import mui.icons.material.ExpandMore
import mui.icons.material.RestaurantMenu
import mui.material.Collapse
import mui.material.List
import mui.material.ListItemButton
import mui.material.ListItemIcon
import mui.material.ListItemText
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.nav
import react.useState
import web.cssom.px

external interface NavigatorProps : Props {
    var recipes: List<Recipe>
    var onRecipeClick: (Recipe) -> Unit
}

val Navigator = FC<NavigatorProps> { props ->
    val recipes = props.recipes
    var recipesExpanded by useState(false)

    List {
        component = nav
        ListItemButton {
            onClick = { recipesExpanded = !recipesExpanded }

            ListItemText {
                primary = ReactNode("Recipes")
            }

            if (recipesExpanded) {
                ExpandLess()
            } else {
                ExpandMore()
            }
        }

        Collapse {
            this.`in` = recipesExpanded
            timeout = "auto"

            List {
                component = div
                disablePadding = true

                recipes.forEach { recipe ->
                    ListItemButton {
                        onClick = { props.onRecipeClick(recipe) }
                        sx = jso { paddingLeft = 32.px }
                        ListItemIcon {
                            sx = jso {
                                minWidth = 36.px // or even 32.px
                            }
                            RestaurantMenu()
                        }
                        ListItemText {
                            primary = ReactNode(recipe.title)
                        }
                    }
                }
            }
        }
    }
}