package com.example.notereminder.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.NoteTagCrossRef
import com.example.notereminder.data.entities.Tag

data class NoteWithTags(
    @Embedded
    private val note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "tagId",
        associateBy = Junction(NoteTagCrossRef::class)
    )
    val tags: List<Tag>,
)
