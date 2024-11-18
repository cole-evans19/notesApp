package com.example.notesapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "NoteListViewModel"

class NoteListViewModel : ViewModel() {

    private val noteRepository = NoteRepository.get()

    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")

    private val _notes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    val notes: StateFlow<List<Note>>
        get() = _notes.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        // Load from both local database and Firestore
        viewModelScope.launch {
            // Local database
            noteRepository.getNotes().collect { localNotes ->
                _notes.value = localNotes
            }
        }

        // Firestore
        notesCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Log the error or handle it
                return@addSnapshotListener
            }

            val firestoreNotes = snapshot?.documents?.mapNotNull { doc ->
                val title = doc.getString("Title")
                val content = doc.getString("Note")
                val id = doc.id // Use Firestore document ID as note ID

                if (title != null && content != null) {
                    Note(
                        id = id, // Firestore ID is directly used as String
                        title = title,
                        description = content
                    )
                } else null
            }.orEmpty()

            // Combine with local notes
            _notes.value = firestoreNotes
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            // Add to local database
            noteRepository.addNote(note)

            // Add to Firestore
            val noteMap = mapOf(
                "Title" to note.title,
                "Note" to note.description
            )
            notesCollection.document(note.id).set(noteMap) // Use the String ID
        }
    }

    fun deleteNote(noteId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            // Delete from local database
            noteRepository.deleteNote(noteId)

            // Delete from Firestore
            notesCollection.document(noteId.toString()).delete()

            // Update in-memory state
            _notes.value = _notes.value.filter { it.uuid != noteId }
        }
    }
}
