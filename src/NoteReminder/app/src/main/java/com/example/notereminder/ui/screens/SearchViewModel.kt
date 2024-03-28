package com.example.notereminder.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.NotesRepository
import com.example.notereminder.data.entities.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    /**
     * Holds home ui state. The list of items are retrieved from [NotesRepository] and mapped to
     * [SearchUiState]
     */
    var searchUiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())

    init {
        searchUiState.flatMapLatest { uiState ->
            notesRepository.getSearchedNoteWithTagsStream(uiState.text)
        }.onEach { newData ->
            searchUiState.value = SearchUiState(
                noteWithTagsList = newData,
                text = searchUiState.value.text,
            )
        }.launchIn(viewModelScope)
    }

    fun changeText(newText: String) {
        searchUiState.value = SearchUiState(
            text = newText,
        )
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
}

data class SearchUiState(
    val text: String = "",
    val noteWithTagsList: List<NoteWithTags> = listOf(),
)