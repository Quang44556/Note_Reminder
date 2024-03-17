package com.example.notereminder.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.notereminder.data.NoteWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteTagCrossRefDao {
    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesWithTags(): Flow<List<NoteWithTags>>

    @Transaction
    @Query("SELECT * FROM notes WHERE noteId = :id")
    fun getNoteWithTags(id: Int): Flow<NoteWithTags>

    @Query("INSERT INTO noteTagCrossRef(noteReferenceId, tagReferenceId) VALUES (:noteId, :tagId)")
    suspend fun insert(noteId: Long, tagId: Long)
}