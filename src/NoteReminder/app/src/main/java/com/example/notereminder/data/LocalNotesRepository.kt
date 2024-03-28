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

    override fun getSearchedNoteWithTagsStream(text: String): Flow<List<NoteWithTags>> =
        noteDao.getSearchedNoteWithTags(text)


    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    /**
     * use [updateNote] to update [Note] in [NoteWithTags] in database
     * then update each [Tag] in [NoteWithTags] in database
     */
    override suspend fun updateNoteAndTags(noteWithTags: NoteWithTags) {
        updateNote(noteWithTags.note)
        noteWithTags.tags.forEach {
            tagDao.updateTag(it)
        }
    }

    override suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    override suspend fun insertTag(tag: Tag) = tagDao.insertTag(tag)

    override suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)
    override suspend fun deleteNote(note: Note) = noteDao.deleteNode(note)

    /**
     * use [deleteTag] to delete each [Tag] in [NoteWithTags] from database
     * then use [deleteNote] to delete [Note] in [NoteWithTags] from database
     */
    override suspend fun deleteNoteWithTags(noteWithTags: NoteWithTags) {
        noteWithTags.tags.forEach {
            deleteTag(it)
        }
        deleteNote(noteWithTags.note)
    }
}
