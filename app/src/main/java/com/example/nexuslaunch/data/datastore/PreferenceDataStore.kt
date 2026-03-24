package com.example.nexuslaunch.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.nexuslaunch.domain.model.LauncherPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("nexus_prefs")

@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val SHOW_SUGGESTIONS = booleanPreferencesKey("show_suggestions")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val preferences: Flow<LauncherPreferences> = context.dataStore.data.map { prefs ->
        LauncherPreferences(
            gridColumns = prefs[Keys.GRID_COLUMNS] ?: 4,
            showSuggestedApps = prefs[Keys.SHOW_SUGGESTIONS] ?: true,
            darkTheme = prefs[Keys.DARK_THEME] ?: false
        )
    }

    suspend fun updateGridColumns(columns: Int) {
        context.dataStore.edit { it[Keys.GRID_COLUMNS] = columns }
    }

    suspend fun updateShowSuggestions(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_SUGGESTIONS] = show }
    }

    suspend fun updateDarkTheme(dark: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = dark }
    }
}