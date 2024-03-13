package com.example.notereminder.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.NotesRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NoteDetailViewMode(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
) : ViewModel() {
    private val noteId: Int = checkNotNull(savedStateHandle[NoteDetailDestination.itemIdArg])

    var noteDetailUiState: NoteDetailUiState by mutableStateOf(NoteDetailUiState())

    init {
        getNoteWithTags()
    }

    private fun getNoteWithTags() {
        viewModelScope.launch {
            noteDetailUiState = notesRepository.getNoteWithTagsStream(noteId)
                .filterNotNull()
                .map { NoteDetailUiState(it) }
                .first()
        }
    }

    fun updateUiState(noteWithTags: NoteWithTags) {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = NoteWithTags(
                note = noteWithTags.note,
                tags = noteWithTags.tags
            )
        )
        viewModelScope.launch {
            notesRepository.updateNoteAndTags(noteWithTags)
        }
    }
}

data class NoteDetailUiState(val noteWithTags: NoteWithTags = NoteWithTags())