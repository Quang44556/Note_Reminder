package com.example.notereminder.data

import com.example.notereminder.data.daos.NoteDao
import com.example.notereminder.data.daos.NoteTagCrossRefDao
import com.example.notereminder.data.daos.TagDao
import com.example.notereminder.data.entities.Note
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
//
//    override fun getItemStream(id: Int): Flow<Note?> = noteDao.getItem(id)
//
//    override suspend fun insertItem(item: Note) = noteDao.insert(item)
//
//    override suspend fun deleteItem(item: Note) = noteDao.delete(item)
//
//    override suspend fun updateItem(item: Note) = noteDao.update(item)
}
