package com.jacagen.jrecipe.model

typealias Tag = String

data class TagDefinition(
    val tag: Tag?,
    val parent: Tag? = null,
    val aliases: Set<String> =  emptySet()
)