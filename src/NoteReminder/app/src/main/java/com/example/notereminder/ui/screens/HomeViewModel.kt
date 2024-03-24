package com.example.notereminder.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.NotesRepository
import com.example.notereminder.data.entities.Tag
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    /**
     * Holds home ui state. The list of items are retrieved from [NotesRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        notesRepository.getAllNotesWithTagsStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    /**
     * update note in database
     */
    fun updateNote(noteWithTags: NoteWithTags) {
        viewModelScope.launch {
            notesRepository.updateNote(noteWithTags.note)
        }
    }

    /**
     * delete tag from database
     */
    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            notesRepository.deleteTag(tag)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(val noteWithTagsList: List<NoteWithTags> = listOf())