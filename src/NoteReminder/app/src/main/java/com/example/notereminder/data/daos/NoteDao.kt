package com.example.notereminder.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.example.notereminder.data.entities.Note

@Dao
interface NoteDao {
    @Update
    suspend fun updateNote(note: Note)

    @Insert
    suspend fun insertNote(note: Note): Long
}