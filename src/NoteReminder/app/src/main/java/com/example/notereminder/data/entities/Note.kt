package com.example.notereminder.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId: Int = 0,
    val title: String = "",
    val content: String = "",
    val isMarked: Boolean = false,
    val createdDate: Date = Date(),
    val reminderDate: Date = Date(),
)
