package com.example.notesapp.presentation.ui

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/** The user's light/dark choice, stored on the device and applied app-wide. */
object ThemePreference {
    const val MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    const val MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES

    private const val PREFS = "settings"
    private const val KEY = "night_mode"

    fun get(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY, MODE_SYSTEM)

    fun set(context: Context, mode: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY, mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /** Called once at process start, before any activity draws. */
    fun apply(context: Context) = AppCompatDelegate.setDefaultNightMode(get(context))
}
