package com.example.notereminder.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notereminder.DEFAULT_ID
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var noteId: Long = DEFAULT_ID,
    val title: String = "",
    val content: String = "",
    val isMarked: Boolean = false,
    val createdDate: Date = Date(),
    val reminderDate: Date = Date(),
)
