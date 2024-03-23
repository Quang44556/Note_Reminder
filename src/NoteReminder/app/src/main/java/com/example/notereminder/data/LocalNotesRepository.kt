package com.example.notereminder.data

import com.example.notereminder.data.daos.NoteDao
import com.example.notereminder.data.daos.TagDao
import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.Tag
import kotlinx.coroutines.flow.Flow

class LocalNotesRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
) : NotesRepository {
    override fun getAllNotesWithTagsStream(): Flow<List<NoteWithTags>> =
        noteDao.getAllNotesWithTags()

    override fun getNoteWithTagsStream(id: Int): Flow<NoteWithTags> =
        noteDao.getNoteWithTags(id)

    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    override suspend fun updateNoteAndTags(noteWithTags: NoteWithTags) {
        updateNote(noteWithTags.note)
        noteWithTags.tags.forEach {
            tagDao.updateTag(it)
        }
    }

    override suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    override suspend fun insertTag(tag: Tag) = tagDao.insertTag(tag)

    override suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)
    override suspend fun deleteNode(note: Note) = noteDao.deleteNode(note)

    override suspend fun deleteNoteWithTags(noteWithTags: NoteWithTags) {
        noteWithTags.tags.forEach {
            deleteTag(it)
        }
        deleteNode(noteWithTags.note)
    }
}
