package com.jacagen.jrecipe.data.dao.mongodb

import com.jacagen.jrecipe.data.dao.EvernoteNoteDao
import com.jacagen.jrecipe.model.EvernoteNote
import com.jacagen.jrecipe.model.EvernoteNoteSummary
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.toList
import org.bson.Document

class MongoEvernoteNoteDao(private val collection: MongoCollection<EvernoteNote>) : EvernoteNoteDao {

    override suspend fun getSummaries(): List<EvernoteNoteSummary> {
        val summary = collection.withDocumentClass(EvernoteNoteSummary::class.java)
        return summary.find().projection(Document(mapOf("_id" to 1, "title" to 1))).toList()
    }

    override suspend fun getAll(): List<EvernoteNote> {
        return collection.find().toList()
    }

    override suspend fun saveNotesToMongo(notes: List<EvernoteNote>) {
        // Remove all existing documents
        collection.deleteMany(Document())  // or Filters.empty()

        // Insert new notes
        collection.insertMany(notes)

        println("Inserted ${notes.size} Evernote notes into MongoDB.")
    }
}