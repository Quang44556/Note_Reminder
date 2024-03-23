package com.example.notereminder.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.notereminder.data.NoteWithTags
import com.example.notereminder.data.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Update
    suspend fun updateNote(note: Note)

    @Insert
    suspend fun insertNote(note: Note): Long

    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesWithTags(): Flow<List<NoteWithTags>>

    @Transaction
    @Query("SELECT * FROM notes WHERE noteId = :id")
    fun getNoteWithTags(id: Int): Flow<NoteWithTags>

    @Delete
    suspend fun deleteNode(note: Note)
}