package com.jacagen.jrecipe

// web/src/jsMain/kotlin/Main.kt
import com.jacagen.jrecipe.component.App
import react.create
import react.dom.client.createRoot
import web.dom.document
import web.dom.ElementId

fun main() {
    val container = document.getElementById(ElementId("root")) ?: error("Missing root element")
    createRoot(container).render(App.create())
}