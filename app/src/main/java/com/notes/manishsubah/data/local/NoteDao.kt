package com.notes.manishsubah.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.notes.manishsubah.domain.model.Note

@Dao
interface NoteDao {

    /** Returns the new row id. REPLACE lets Undo restore a note under its original id. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Delete
    suspend fun deleteAll(notes: List<Note>)

    /** Board order: open before done; pinned open notes first; newest first within each group. */
    @Query(
        "SELECT * FROM notes_table " +
            "ORDER BY is_done ASC, " +
            "CASE WHEN is_done = 0 THEN is_pinned ELSE 0 END DESC, " +
            "CASE WHEN is_done = 1 THEN updated_at ELSE created_at END DESC, " +
            "id DESC"
    )
    fun getAllNotes(): LiveData<List<Note>>
}
