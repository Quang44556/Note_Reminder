package com.example.notereminder.data

import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.Tag
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotesWithTagsStream(): Flow<List<NoteWithTags>>
    fun getNoteWithTagsStream(id: Int): Flow<NoteWithTags>
    suspend fun updateNote(note: Note)
    suspend fun updateNoteAndTags(noteWithTags: NoteWithTags)
    suspend fun insertNote(note: Note): Long
    suspend fun insertTag(tag: Tag): Long
    suspend fun deleteTag(tag: Tag)
}
