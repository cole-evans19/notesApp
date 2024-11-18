package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "NoteListViewModel"

class NoteListViewModel : ViewModel() {

    private val noteRepository = NoteRepository.get()

    private val _notes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    val notes: StateFlow<List<Note>>
        get() = _notes.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.getNotes().collect {
                _notes.value = it
            }
        }
    }

    fun deleteNote(noteId: UUID) {
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.deleteNote(noteId)
            _notes.value = _notes.value.filter { it.id != noteId.toString() }
        }
    }


}
