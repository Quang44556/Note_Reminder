package com.example.notereminder.data

import android.content.Context

interface AppContainer {
    val notesRepository: NotesRepository
}

/**
 * [AppContainer] implementation that provides instance of [LocalNotesRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [NotesRepository]
     */
    override val notesRepository: NotesRepository by lazy {
        LocalNotesRepository(
            AppDatabase.getDatabase(context).noteDao(),
            AppDatabase.getDatabase(context).tagDao(),
        )
    }
}