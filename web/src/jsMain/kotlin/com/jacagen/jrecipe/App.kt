package com.jacagen.jrecipe

// web/src/jsMain/kotlin/App.kt
import react.FC
import react.Props
import react.dom.html.ReactHTML.div

val App = FC<Props> {
    div {
        +"Hello from Kotlin/React!"
    }
}