package com.example.notereminder.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.notereminder.data.entities.Tag

@Dao
interface TagDao {
    @Update
    suspend fun updateTag(tag: Tag)

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag): Long

    @Delete
    suspend fun deleteTag(tag: Tag)
}