package com.example.notereminder.data

import com.example.notereminder.data.entities.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotesWithTagsStream(): Flow<List<NoteWithTags>>
    fun getNoteWithTagsStream(id: Int): Flow<NoteWithTags>
    suspend fun updateNote(note: Note)
    suspend fun updateNoteAndTags(noteWithTags: NoteWithTags)
//
//    /**
//     * Retrieve an note from the given data source that matches with the [id].
//     */
//    fun getItemStream(id: Int): Flow<Note?>
//
//    /**
//     * Insert note in the data source
//     */
//    suspend fun insertItem(item: Note)
//
//    /**
//     * Delete note from the data source
//     */
//    suspend fun deleteItem(item: Note)
//

}
