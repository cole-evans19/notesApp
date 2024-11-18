package com.example.notesapp

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs

class DeleteNoteDialogFragment : DialogFragment() {
    private val args: DeleteNoteFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create the alert dialog builder
        val builder = AlertDialog.Builder(requireContext())

        // Set the title and message
        builder.setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                // Notify that the user wants to delete the note
                setFragmentResult(REQUEST_KEY_DELETE, bundleOf(BUNDLE_KEY_NOTE_ID to args.deleteTicket))
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        return builder.create()
    }

    companion object {
        const val REQUEST_KEY_DELETE = "REQUEST_KEY_DELETE"
        const val BUNDLE_KEY_NOTE_ID = "BUNDLE_KEY_NOTE_ID"
    }
}
