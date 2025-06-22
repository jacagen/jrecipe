package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.model.Recipe
import com.jacagen.jrecipe.model.TagDefinition
import js.objects.jso
import mui.icons.material.ExpandLess
import mui.icons.material.ExpandMore
import mui.icons.material.Label
import mui.icons.material.RestaurantMenu
import mui.material.*
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.div
import react.useState
import web.cssom.px

external interface NavigatorProps : Props {
    var recipes: List<Recipe>
    var onRecipeClick: (Recipe) -> Unit
    var tags: List<TagDefinition>
}

val Navigator = FC<NavigatorProps> { props ->
    val recipes = props.recipes
    var recipesExpanded by useState(true)

    val tags = props.tags
    var tagsExpanded by useState(false)

    List {  // It might be nice to create a component for this someday
        ListItemButton {
            onClick = { tagsExpanded = !tagsExpanded }

            ListItemText {
                primary = ReactNode("Tags")
            }

            if (tagsExpanded) {
                ExpandLess()
            } else {
                ExpandMore()
            }
        }

        Collapse {
            this.`in` = tagsExpanded
            timeout = "auto"

            List {
                component = div
                disablePadding = true

                tags.forEach { tag ->
                    ListItemButton {
                        sx = jso { paddingLeft = 32.px }
                        ListItemIcon {
                            sx = jso {
                                minWidth = 36.px // or even 32.px
                            }
                            Label()
                        }
                        ListItemText {
                            primary = ReactNode(tag.tag)
                        }
                    }
                }
            }
        }


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

