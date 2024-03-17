package com.example.notereminder.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notereminder.DEFAULT_ID
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.NotesRepository
import com.example.notereminder.data.entities.Tag
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

    fun updateNote(noteWithTags: NoteWithTags) {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = NoteWithTags(
                note = noteWithTags.note,
                tags = noteWithTags.tags
            )
        )
        insertOrUpdateDatabase(noteWithTags)
    }

    fun updateShowingDialog() {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = noteDetailUiState.noteWithTags,
            isShowingDialog = !noteDetailUiState.isShowingDialog
        )
    }

    private fun insertOrUpdateDatabase(noteWithTags: NoteWithTags) {
        viewModelScope.launch {
            if (noteWithTags.note.noteId == DEFAULT_ID) {
                noteDetailUiState.noteWithTags.note.noteId =
                    notesRepository.insertNote(noteWithTags.note)
            } else {
                notesRepository.updateNoteAndTags(noteWithTags)
            }
        }
    }

    fun insertTag(tag: Tag) {
        tag.noteBelongedToId = noteDetailUiState.noteWithTags.note.noteId

        noteDetailUiState.noteWithTags.tags += Tag(
            name = tag.name,
            noteBelongedToId = noteDetailUiState.noteWithTags.note.noteId
        )
        val size = noteDetailUiState.noteWithTags.tags.size

        viewModelScope.launch {
            noteDetailUiState.noteWithTags.tags[size - 1].tagId = notesRepository.insertTag(tag)
        }
    }

    fun updateTagsInNote(tag: Tag) {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = NoteWithTags(
                note = noteDetailUiState.noteWithTags.note,
                tags = noteDetailUiState.noteWithTags.tags.toMutableList().apply { remove(tag) }
            )
        )

        deleteTag(tag)
    }

    private fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            notesRepository.deleteTag(tag)
        }
    }
}

data class NoteDetailUiState(
    val noteWithTags: NoteWithTags = NoteWithTags(),
    val isShowingDialog: Boolean = false,
)