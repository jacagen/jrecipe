package com.jacagen.jrecipe.service

import com.jacagen.jrecipe.importer.evernote.EvernoteNote
import com.jacagen.jrecipe.model.Recipe
import dev.langchain4j.service.SystemMessage

internal interface EvernoteToLlmConverter {
    @SystemMessage(
        """
        Your job is to take a recipe, and convert it to a standard output format.
        If there is any information about "techniques" in the recipe, include this in the `notes` field (along with any other notes).
        The input recipe will always have an id, and this id must *always* be included as the output id, and *never* changed.  
        The id will always be a UUID4 id.  Be sure to represent it as such.
        Always set the `source` to `EVERNOTE`.
    """
    )
    fun convertRecipe(evernoteRecipe: EvernoteNote): Recipe
}