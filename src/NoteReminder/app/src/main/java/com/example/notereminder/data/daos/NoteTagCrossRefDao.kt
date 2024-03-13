package com.example.notereminder.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.NoteTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteTagCrossRefDao {
    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesWithTags(): Flow<List<NoteWithTags>>

    @Transaction
    @Query("SELECT * FROM notes WHERE noteId = :id")
    fun getNoteWithTags(id: Int): Flow<NoteWithTags>

    @Update
    fun updateTagsInNote(noteTagCrossRef: NoteTagCrossRef)
}