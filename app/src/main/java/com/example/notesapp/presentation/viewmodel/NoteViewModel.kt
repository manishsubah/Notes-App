package com.example.notesapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.local.NotesDatabase
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.domain.model.Note
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        NoteRepository(NotesDatabase.getDatabase(application).getNoteDao())

    val allNotes: LiveData<List<Note>> = repository.allNotes

    fun addNote(text: String) = viewModelScope.launch {
        repository.insert(Note(text = text))
    }

    fun editNote(note: Note, text: String) = viewModelScope.launch {
        if (text != note.text) {
            repository.update(note.copy(text = text, updatedAt = System.currentTimeMillis()))
        }
    }

    fun setDone(note: Note, done: Boolean) = viewModelScope.launch {
        repository.update(note.copy(isDone = done, updatedAt = System.currentTimeMillis()))
    }

    fun setPinned(note: Note, pinned: Boolean) = viewModelScope.launch {
        repository.update(note.copy(isPinned = pinned))
    }

    fun deleteNotes(notes: List<Note>) = viewModelScope.launch {
        repository.deleteAll(notes)
    }

    /** Undo for a delete or a clear: puts the same notes back under their original ids. */
    fun restoreNotes(notes: List<Note>) = viewModelScope.launch {
        repository.restore(notes)
    }
}
