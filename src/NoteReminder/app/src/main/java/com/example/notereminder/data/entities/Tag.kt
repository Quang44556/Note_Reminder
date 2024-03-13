package com.example.notereminder.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val tagId: Int = 0,
    val name: String = "",
)
