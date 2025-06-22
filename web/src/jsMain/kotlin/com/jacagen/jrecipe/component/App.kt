package com.jacagen.jrecipe.component

// web/src/jsMain/kotlin/App.kt
import mui.material.Button
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import mui.material.ButtonColor
import mui.material.ButtonVariant
import react.StrictMode

val App = FC<Props> {
    StrictMode {
        ThreeColumnLayout()
    }
}