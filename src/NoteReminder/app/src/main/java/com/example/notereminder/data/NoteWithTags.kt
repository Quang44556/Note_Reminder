package com.example.notereminder.data

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.Relation
import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.Tag

/**
 * Data class represent a [Note] with multiple [Tag] in it
 */
data class NoteWithTags(
    @Embedded val note: Note = Note(),
    @Relation(
        parentColumn = "noteId",
        entityColumn = "noteBelongedToId",
    )
    val tags: MutableList<Tag> = mutableListOf(),
)

