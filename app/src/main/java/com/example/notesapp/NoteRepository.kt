package com.example.notesapp

import android.content.Context
import androidx.room.Room
import com.example.notesapp.database.NoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "note-database"

class NoteRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope) {

    private val database: NoteDatabase = Room.databaseBuilder(
        context.applicationContext,
        NoteDatabase::class.java,
        DATABASE_NAME
    )
        .build()

    fun getNotes(): Flow<List<Note>> = database.noteDao().getNotes()
    fun getNote(id: UUID): Flow<Note> = database.noteDao().getNotes(id)
    fun deleteNote(noteId: UUID) {
        coroutineScope.launch {
            database.noteDao().deleteNoteById(noteId.toString())
        }
    }
    fun updateNote(note: Note) {
        coroutineScope.launch {
            database.noteDao().updateNote(note)
        }
    }
    fun addNote(note: Note) {
        coroutineScope.launch {
            database.noteDao().addNote(note)
        }
    }

    companion object {
        private var INSTANCE: NoteRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = NoteRepository(context)
            }
        }

        fun get(): NoteRepository {
            return INSTANCE ?:
            throw IllegalStateException(
                "NoteRepository must be initialized")
        }
    }
}
