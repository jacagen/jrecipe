package com.jacagen.jrecipe.model

object TagCatalog {
    val tagMap: Map<String, Tag>
    init {
        val tagMapBuilder = mutableMapOf<String, Tag>()
        tagsDefinitions.forEach { (tag, parent, aliases) ->
            tagMapBuilder[tag.normalize()] = tag
            if (parent != null)
                tagMapBuilder[parent.normalize()] = parent
            aliases.forEach { tagMapBuilder[it.normalize()] = tag }
        }
        tagMap = tagMapBuilder.toMap()
    }

    operator fun get(name: String): Tag? = tagMap[name.normalize()]

    private fun Tag.normalize() = lowercase().replace(" ", "")

}
