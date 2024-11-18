package com.example.notesapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notesapp.Note
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id=:id")
            fun getNotes(id: UUID) : Flow<Note>

    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNoteById(noteId: String)

    @Update
    fun updateNote(note: Note)

    @Insert
    fun addNote(note: Note)

    @Delete
    fun deleteNote(note: Note)


}
