package com.example.notereminder.data

import com.example.notereminder.data.daos.NoteDao
import com.example.notereminder.data.daos.NoteTagCrossRefDao
import com.example.notereminder.data.daos.TagDao

class LocalNotesRepository(
    private val noteDao: NoteDao,
    private val tagDao: TagDao,
    private val noteTagCrossRefDao: NoteTagCrossRefDao
) : NotesRepository {
//    override fun getAllItemsStream(): Flow<List<Note>> = noteDao.getAllItems()
//
//    override fun getItemStream(id: Int): Flow<Note?> = noteDao.getItem(id)
//
//    override suspend fun insertItem(item: Note) = noteDao.insert(item)
//
//    override suspend fun deleteItem(item: Note) = noteDao.delete(item)
//
//    override suspend fun updateItem(item: Note) = noteDao.update(item)
}
