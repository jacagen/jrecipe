@file:OptIn(ExperimentalUuidApi::class)

package com.jacagen.jrecipe.importer.evernote

import org.w3c.dom.Element
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.uuid.ExperimentalUuidApi

private val evernoteInstantFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

internal suspend fun loadEvernoteToMongo() {
    val resource =
        EvernoteNote::class.java.classLoader.getResource("recipeSource/Recipes.enex") ?: error("Resource not found")
    val file = File(resource.toURI())
    val notes = parseEnexFile(file)
    saveNotesToMongo(notes)
}

private fun parseEnexFile(file: File): List<EvernoteNote> {
    val notes = mutableListOf<EvernoteNote>()

    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = docBuilder.parse(file)
    val noteNodes = doc.getElementsByTagName("note")

    for (i in 0 until noteNodes.length) {
        val noteElement = noteNodes.item(i) as Element

        var author: String? = null
        var source: String? = null
        var sourceUrl: String? = null
        var sourceApplication: String? = null
        var subjectDate: String? = null
        var contentClass: String? = null

        // Parse <note-attributes> children into key-value pairs
        val noteAttrNodes = noteElement.getElementsByTagName("note-attributes")
        if (noteAttrNodes.length > 0) {
            val noteAttrElem = noteAttrNodes.item(0) as Element
            val children = noteAttrElem.childNodes
            for (j in 0 until children.length) {
                val child = children.item(j)
                if (child is Element) {
                    when (child.tagName) {
                        "author" -> author = child.textContent.trim()
                        "source" -> source = child.textContent.trim()
                        "source-url" -> sourceUrl = child.textContent.trim()
                        "source-application" -> sourceApplication = child.textContent.trim()
                        "subject-date" -> subjectDate = child.textContent.trim()
                        "content-class" -> contentClass = child.textContent.trim()
                    }
                }
            }
        }

        val title = noteElement.getElementsByTagName("title").item(0).textContent.trim()
        val createdText = noteElement.getElementsByTagName("created").item(0).textContent.trim()
        val updatedText = noteElement.getElementsByTagName("updated").item(0).textContent.trim()
        val created = parseEvernoteTimestamp(createdText)
        val updated = parseEvernoteTimestamp(updatedText)
        val content = noteElement.getElementsByTagName("content").item(0).textContent.trim()


        val tagNodes = noteElement.getElementsByTagName("tag")
        val tags = mutableSetOf<String>()
        for (j in 0 until tagNodes.length) {
            tags.add(tagNodes.item(j).textContent.trim())
        }

        notes.add(
            EvernoteNote(
                title = title,
                created = created,
                updated = updated,
                author = author,
                source = source,
                sourceUrl = sourceUrl,
                sourceApplication = sourceApplication,
                subjectDate = subjectDate,
                contentClass = contentClass,
                content = content,
                tags = tags,
            )
        )
    }

    return notes
}

private fun parseEvernoteTimestamp(ts: String): Instant {
    return LocalDateTime.parse(ts, evernoteInstantFormatter).toInstant(ZoneOffset.UTC)
}