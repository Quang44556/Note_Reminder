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
import com.example.notereminder.ui.screens.SearchViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                noteReminderApplication().scheduler,
                noteReminderApplication().container.notesRepository
            )
        }
        // Initializer for NoteDetailViewMode
        initializer {
            NoteDetailViewMode(
                noteReminderApplication().scheduler,
                this.createSavedStateHandle(),
                noteReminderApplication().container.notesRepository
            )
        }
        // Initializer for SearchViewModel
        initializer {
            SearchViewModel(
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