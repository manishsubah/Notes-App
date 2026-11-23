package com.notes.manishsubah

import android.app.Application
import com.notes.manishsubah.presentation.ui.ThemePreference

class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply the saved light/dark choice before the first activity draws, so there is no flash.
        ThemePreference.apply(this)
    }
}
