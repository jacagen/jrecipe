package com.jacagen.jrecipe

// web/src/jsMain/kotlin/Main.kt
import react.create
import react.dom.client.createRoot
import web.dom.document
import web.html.HTML.div
import web.dom.ElementId

fun main() {
    val container = document.getElementById(ElementId("root")) ?: error("Missing root element")
    createRoot(container).render(App.create())
}