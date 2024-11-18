package com.example.notesapp

import NoteDetailViewModel
import NoteDetailViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.notesapp.databinding.FragmentNoteDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "NoteDetailFragment"

class NoteDetailFragment : Fragment() {
    private val args: NoteDetailFragmentArgs by navArgs()
    private val noteDetailViewModel: NoteDetailViewModel by viewModels {
        NoteDetailViewModelFactory(args.noteId)
    }
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "cannot access the view because it is not null"
        }
    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            noteTitle.doOnTextChanged { text, _, _, _ ->
                noteDetailViewModel.updateNote { oldNote ->
                    val updatedNote = oldNote.copy(title = text.toString())
                    updateFirebaseNote(updatedNote) // Update Firestore
                    updatedNote
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteDetailViewModel.note.collect { note ->
                    note?.let { updateUi(it) }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference
    }

    private fun updateUi(note: Note) {
        binding.apply {
            if (noteTitle.text.toString() != note.title) {
                noteTitle.setText(note.title)
            }
        }
    }

    private fun updateFirebaseNote(note: Note) {
        val noteData = mapOf(
            "Note" to note.description,
            "Title" to note.title
        )
        notesCollection.document(note.id.toString())
            .set(noteData)
            .addOnSuccessListener {
                // Log or handle successful update if needed
            }
            .addOnFailureListener { e ->
                // Log or handle the error
                e.printStackTrace()
            }
    }
}
