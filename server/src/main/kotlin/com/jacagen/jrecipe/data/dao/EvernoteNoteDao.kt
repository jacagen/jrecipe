package com.jacagen.jrecipe.data.dao

import com.jacagen.jrecipe.model.EvernoteNote
import com.jacagen.jrecipe.model.EvernoteNoteSummary

interface EvernoteNoteDao {
    suspend fun getSummaries(): List<EvernoteNoteSummary>
    suspend fun getAll(): List<EvernoteNote>
    suspend fun saveNotesToMongo(notes: List<EvernoteNote>)
}