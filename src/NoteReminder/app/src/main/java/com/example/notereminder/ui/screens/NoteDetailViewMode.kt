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
    private val noteId: Int = checkNotNull(savedStateHandle[NoteDetailDestination.ITEM_ID_ARG])

    /**
     * Holds current ui state
     */
    var noteDetailUiState: NoteDetailUiState by mutableStateOf(NoteDetailUiState())

    /**
     * Holds deleted tags in current note
     */
    private val deletedTags: MutableList<Tag> = mutableListOf()

    init {
        getNoteWithTags()
    }

    /**
     * get current [NoteWithTags] in database
     */
    private fun getNoteWithTags() {
        viewModelScope.launch {
            noteDetailUiState = notesRepository.getNoteWithTagsStream(noteId)
                .filterNotNull()
                .map { NoteDetailUiState(it) }
                .first()
        }
    }

    /**
     * update [NoteDetailUiState]
     */
    fun updateNoteDetailUiState(noteWithTags: NoteWithTags) {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = NoteWithTags(
                note = noteWithTags.note,
                tags = noteWithTags.tags
            ),
            isShowingCheckIcon = noteDetailUiState.isShowingCheckIcon
        )

        // if check icon is not showing, show it
        if (!noteDetailUiState.isShowingCheckIcon) {
            updateShowingCheckIcon()
        }
    }

    /**
     * show or hide dialog add tag
     */
    fun updateShowingDialogAddTag() {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = noteDetailUiState.noteWithTags,
            isShowingDialogAddTag = !noteDetailUiState.isShowingDialogAddTag,
            isShowingCheckIcon = noteDetailUiState.isShowingCheckIcon,
        )
    }

    /**
     * show or hide dialog delete current note
     */
    fun updateShowingDialogDeleteNote() {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = noteDetailUiState.noteWithTags,
            isShowingDialogDeleteNote = !noteDetailUiState.isShowingDialogDeleteNote,
            isShowingCheckIcon = noteDetailUiState.isShowingCheckIcon,
        )
    }

    /**
     * show or hide dialog remind to save note
     */
    fun updateShowingDialogSaveNote() {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = noteDetailUiState.noteWithTags,
            isShowingDialogSaveNote = !noteDetailUiState.isShowingDialogSaveNote,
            isShowingCheckIcon = noteDetailUiState.isShowingCheckIcon,
        )
    }

    /**
     * show or hide check icon
     */
    private fun updateShowingCheckIcon() {
        noteDetailUiState = NoteDetailUiState(
            noteWithTags = noteDetailUiState.noteWithTags,
            isShowingCheckIcon = !noteDetailUiState.isShowingCheckIcon,
            isShowingDialogAddTag = noteDetailUiState.isShowingDialogAddTag
        )
    }

    /**
     * if [NoteWithTags] has not been already in database, insert it to database
     * Otherwise, update it in database
     */
    fun insertOrUpdateNoteWithTags() {
        if (noteDetailUiState.noteWithTags.note.noteId == DEFAULT_ID) {
            insertNoteWithTags()
        } else {
            updateNoteWithTags()
        }
        updateShowingCheckIcon()
    }

    /**
     *insert [NoteWithTags] to database
     */
    private fun insertNoteWithTags() {
        viewModelScope.launch {
            noteDetailUiState.noteWithTags.note.noteId =
                notesRepository.insertNote(noteDetailUiState.noteWithTags.note)
            insertTagsToDatabase()
            deleteTagsInDatabase()
        }
    }

    /**
     *Update [NoteWithTags] to database
     */
    private fun updateNoteWithTags() {
        viewModelScope.launch {
            notesRepository.updateNoteAndTags(noteDetailUiState.noteWithTags)
            insertTagsToDatabase()
            deleteTagsInDatabase()
        }
    }

    /**
     *insert all [Tag] in current [NoteWithTags] to database
     */
    private suspend fun insertTagsToDatabase() {
        noteDetailUiState.noteWithTags.tags.forEach { tag ->
            tag.noteBelongedToId = noteDetailUiState.noteWithTags.note.noteId
            notesRepository.insertTag(tag)
        }
    }

    /**
     *Delete all [Tag] in [deletedTags] from database
     */
    private suspend fun deleteTagsInDatabase() {
        deletedTags.forEach { tag ->
            notesRepository.deleteTag(tag)
        }
    }

    /**
     *Insert a [Tag] to [NoteDetailUiState]
     */
    fun insertTagToUiState(tag: Tag) {
        tag.noteBelongedToId = noteDetailUiState.noteWithTags.note.noteId

        noteDetailUiState.noteWithTags.tags += Tag(
            name = tag.name,
            noteBelongedToId = noteDetailUiState.noteWithTags.note.noteId
        )

        // if check icon is not showing, show it
        if (!noteDetailUiState.isShowingCheckIcon) {
            updateShowingCheckIcon()
        }

        // hide dialog add tag
        updateShowingDialogAddTag()
    }

    /**
     *Delete a [Tag] from [NoteDetailUiState]
     */
    fun deleteTagInUiState(tag: Tag) {
        deletedTags.add(tag)

        noteDetailUiState = NoteDetailUiState(
            noteWithTags = NoteWithTags(
                note = noteDetailUiState.noteWithTags.note,
                tags = noteDetailUiState.noteWithTags.tags.toMutableList().apply { remove(tag) }
            ),
        )

        // if check icon is not showing, show it
        if (!noteDetailUiState.isShowingCheckIcon) {
            updateShowingCheckIcon()
        }
    }

    /**
     *Delete a [NoteWithTags] from database
     */
    fun deleteNoteWithTags() {
        viewModelScope.launch {
            notesRepository.deleteNoteWithTags(noteDetailUiState.noteWithTags)
        }
    }
}

data class NoteDetailUiState(
    val noteWithTags: NoteWithTags = NoteWithTags(),
    val isShowingDialogAddTag: Boolean = false,
    val isShowingDialogDeleteNote: Boolean = false,
    val isShowingCheckIcon: Boolean = false,
    val isShowingDialogSaveNote: Boolean = false,
)