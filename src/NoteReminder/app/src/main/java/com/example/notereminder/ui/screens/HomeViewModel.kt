package com.example.notereminder.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.NotesRepository
import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.Tag
import com.example.notereminder.notification.AndroidAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val scheduler: AndroidAlarmScheduler,
    private val notesRepository: NotesRepository
) : ViewModel() {
    /**
     * Holds home ui state. The list of items are retrieved from [NotesRepository] and mapped to
     * [HomeUiState]
     */
    var homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    init {
        notesRepository.getAllNotesWithTagsStream()
            .onEach { newData ->
                homeUiState.value = HomeUiState(
                    noteWithTagsList = newData,
                    selectedNotes = homeUiState.value.selectedNotes,
                )
            }
            .launchIn(viewModelScope)
    }

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

    /**
     * put [noteWithTags] to selected notes list when user long click [noteWithTags]
     * and when user click [noteWithTags] while app is in multi select mode
     */
    fun putToSelectedNotes(noteWithTags: NoteWithTags) {
        if (homeUiState.value.selectedNotes.contains(noteWithTags)) {
            homeUiState.value = homeUiState.value.copy(
                selectedNotes = homeUiState.value.selectedNotes - noteWithTags
            )
        } else {
            homeUiState.value = homeUiState.value.copy(
                selectedNotes = homeUiState.value.selectedNotes + noteWithTags
            )
        }
    }

    /**
     * exit multi select mode
     */
    fun exitMultiSelectMode() {
        homeUiState.value = homeUiState.value.copy(
            selectedNotes = listOf()
        )
    }

    /**
     *delete all notes which were selected in multi select mode
     */
    fun deleteSelectedNotes() {
        viewModelScope.launch {
            homeUiState.value.selectedNotes.forEach {
                cancelNotification(it.note)
                notesRepository.deleteNoteWithTags(it)
            }
            exitMultiSelectMode()
        }
    }

    /**
     * cancel scheduled notification
     */
    private fun cancelNotification(note: Note) {
        note.let(scheduler::cancel)
    }

    /**
     *show or hide dialog ask user if they are sure to delete selected notes or not
     */
    fun updateShowingDialogDeleteNotes() {
        homeUiState.value = homeUiState.value.copy(
            isShowingDialogDeleteNotes = !homeUiState.value.isShowingDialogDeleteNotes
        )
    }
}

data class HomeUiState(
    val noteWithTagsList: List<NoteWithTags> = listOf(),
    val selectedNotes: List<NoteWithTags> = listOf(),
    val isShowingDialogDeleteNotes: Boolean = false,
)