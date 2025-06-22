@file:JsModule("react-markdown") @file:JsNonModule

package com.jacagen.jrecipe.component


import react.ComponentType
import react.Props

external interface ReactMarkdownProps : Props {
    var children: String
    var remarkPlugins: Array<dynamic>?
}

@JsName("default")
external val ReactMarkdown: ComponentType<ReactMarkdownProps>