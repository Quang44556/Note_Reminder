package com.example.notereminder.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["noteReferenceId", "tagReferenceId"],
    foreignKeys = [
        ForeignKey(entity = Note::class, parentColumns = ["noteId"], childColumns = ["noteReferenceId"]),
        ForeignKey(entity = Tag::class, parentColumns = ["tagId"], childColumns = ["tagReferenceId"])
    ]
)
data class NoteTagCrossRef(
    val noteReferenceId: Int,
    val tagReferenceId: Int,
)
