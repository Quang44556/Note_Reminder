package com.example.notereminder.data

import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.Tag
import java.util.Date

val item1: NoteWithTags = NoteWithTags(
    note = Note(
        noteId = 1,
        title = "title",
        content = "content",
        isMarked = true,
        createdDate = Date(),
        reminderDate = Date(),
    ),
    tags = mutableListOf()
)

val item2: NoteWithTags = NoteWithTags(
    note = Note(
        noteId = 1,
        title = "title",
        content = "content",
        isMarked = false,
        createdDate = Date(),
        reminderDate = Date(),
    ),
    tags = mutableListOf(Tag(1, "12345"), Tag(2, "2"))
)

val item3: NoteWithTags = NoteWithTags(
    note = Note(
        noteId = 1,
        title = "title",
        content = "content",
        isMarked = true,
        createdDate = Date(),
        reminderDate = Date(),
    ),
    tags = mutableListOf(
        Tag(1, "1"),
        Tag(2, "2dsddsdf222"),
        Tag(3, "a346aadasdasassffsds43"),
        Tag(4, "sas2dffsdfsdfsd2"),
        Tag(5, "??saaas??")
    )
)

val tag1: Tag = Tag(1, "123")
