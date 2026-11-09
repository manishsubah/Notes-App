package com.example.notesapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.notesapp.domain.model.Note


@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: Note)
    //suspend fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("Select * from notes_table order by id ASC")
    fun getAllNotes(): LiveData<List<Note>>

}