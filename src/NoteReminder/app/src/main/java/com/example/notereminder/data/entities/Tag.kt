package com.example.notereminder.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var tagId: Long = 0,
    var noteBelongedToId: Long = 0,
    val name: String = "",
)
