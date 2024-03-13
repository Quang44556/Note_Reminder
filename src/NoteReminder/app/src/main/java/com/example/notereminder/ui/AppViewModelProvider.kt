package com.example.notereminder.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notereminder.NoteReminderApplication
import com.example.notereminder.ui.screens.HomeViewModel
import com.example.notereminder.ui.screens.NoteDetailViewMode

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            HomeViewModel(
                noteReminderApplication().container.notesRepository
            )
        }
        // Initializer for ItemEntryViewModel
        initializer {
            NoteDetailViewMode(
                this.createSavedStateHandle(),
                noteReminderApplication().container.notesRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [NoteReminderApplication].
 */
fun CreationExtras.noteReminderApplication(): NoteReminderApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NoteReminderApplication)