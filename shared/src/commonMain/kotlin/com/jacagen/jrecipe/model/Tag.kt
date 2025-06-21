package com.jacagen.jrecipe.model

import kotlinx.serialization.Serializable

typealias Tag = String

@Serializable
data class TagDefinition(
    val tag: Tag,
    val parent: Tag? = null,
    val aliases: Set<String> =  emptySet()
)