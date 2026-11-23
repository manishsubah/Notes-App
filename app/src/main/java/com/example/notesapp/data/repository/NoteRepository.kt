package com.example.notesapp.data.repository

import androidx.lifecycle.LiveData
import com.example.notesapp.data.local.NoteDao
import com.example.notesapp.domain.model.Note

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note): Long = noteDao.insert(note)

    suspend fun restore(notes: List<Note>) = noteDao.insertAll(notes)

    suspend fun update(note: Note) = noteDao.update(note)

    suspend fun delete(note: Note) = noteDao.delete(note)

    suspend fun deleteAll(notes: List<Note>) = noteDao.deleteAll(notes)
}
