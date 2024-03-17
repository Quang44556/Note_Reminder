package com.example.notereminder.data

import com.example.notereminder.data.daos.NoteDao
import com.example.notereminder.data.daos.NoteTagCrossRefDao
import com.example.notereminder.data.daos.TagDao
import com.example.notereminder.data.entities.Note
import com.example.notereminder.data.entities.Tag
import kotlinx.coroutines.flow.Flow

class LocalNotesRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
    private val noteTagCrossRefDao: NoteTagCrossRefDao
) : NotesRepository {
    override fun getAllNotesWithTagsStream(): Flow<List<NoteWithTags>> =
        noteTagCrossRefDao.getAllNotesWithTags()

    override fun getNoteWithTagsStream(id: Int): Flow<NoteWithTags> =
        noteTagCrossRefDao.getNoteWithTags(id)

    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    override suspend fun updateNoteAndTags(noteWithTags: NoteWithTags) {
        updateNote(noteWithTags.note)
        noteWithTags.tags.forEach {
            tagDao.updateTag(it)
        }
        //noteTagCrossRefDao.updateTagsInNote()
    }

    override suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
    override suspend fun insertNoteTagCrossRef(noteId: Long, tagId: Long) =
        noteTagCrossRefDao.insert(noteId, tagId)

    override suspend fun insertTag(tag: Tag) = tagDao.insertTag(tag)

    override suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)
}
