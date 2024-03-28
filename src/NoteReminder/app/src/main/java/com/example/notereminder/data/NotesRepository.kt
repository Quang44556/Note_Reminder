package com.example.notereminder.data

import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.Tag
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    /**
     * Retrieve all the [NoteWithTags] from the the given data source.
     */
    fun getAllNotesWithTagsStream(): Flow<List<NoteWithTags>>

    /**
     * Retrieve a [NoteWithTags] from the the given data source that matches with the [id].
     */
    fun getNoteWithTagsStream(id: Int): Flow<NoteWithTags>

    /**
     * Retrieve all [NoteWithTags] from the the given data source that have title, content, tag matches with the [text].
     */
    fun getSearchedNoteWithTagsStream(text: String): Flow<List<NoteWithTags>>

    /**
     * Update [Note] in the data source
     */
    suspend fun updateNote(note: Note)

    /**
     * Update [NoteWithTags] in the data source
     */
    suspend fun updateNoteAndTags(noteWithTags: NoteWithTags)

    /**
     * Insert [Note] in the data source
     */
    suspend fun insertNote(note: Note): Long

    /**
     * Insert [Tag] in the data source
     */
    suspend fun insertTag(tag: Tag): Long

    /**
     * Delete [Tag] from the data source
     */
    suspend fun deleteTag(tag: Tag)

    /**
     * Delete [Note] from the data source
     */
    suspend fun deleteNote(note: Note)

    /**
     * Delete [NoteWithTags] from the data source
     */
    suspend fun deleteNoteWithTags(noteWithTags: NoteWithTags)
}
