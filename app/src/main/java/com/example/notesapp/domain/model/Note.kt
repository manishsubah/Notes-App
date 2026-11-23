package com.example.notesapp.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Note(
    @ColumnInfo(name = "note_text") val text: String,
    @ColumnInfo(name = "is_done", defaultValue = "0") val isDone: Boolean = false,
    @ColumnInfo(name = "is_pinned", defaultValue = "0") val isPinned: Boolean = false,
    /** Epoch millis; 0 for notes saved before times were recorded. */
    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at", defaultValue = "0") val updatedAt: Long = createdAt,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
