package com.jacagen.jrecipe.component

import com.jacagen.jrecipe.model.RecipeSummary
import com.jacagen.jrecipe.model.TagDefinition
import js.objects.jso
import js.objects.unsafeJso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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


private val scope = MainScope()

external interface NavigatorProps : Props {
    var recipes: List<RecipeSummary>
    var onRecipeClick: suspend (RecipeSummary) -> Unit
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
            timeout = 0

            List {
                component = div
                disablePadding = true

                tags.forEach { tag ->
                    ListItemButton {
                        sx = unsafeJso {
                            paddingLeft = 32.px
                        }

                        ListItemIcon {
                            sx = unsafeJso {
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
            timeout = 0

            List {
                component = div
                disablePadding = true

                recipes.forEach { recipe ->
                    ListItemButton {
                        onClick = { scope.launch { props.onRecipeClick(recipe) } }
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

