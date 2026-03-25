package com.example.nexuslaunch.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexuslaunch.data.datastore.PreferencesDataStore
import com.example.nexuslaunch.domain.model.LauncherPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsDataStore: PreferencesDataStore
) : ViewModel() {

    val preferences: StateFlow<LauncherPreferences> = prefsDataStore.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LauncherPreferences()
        )

    fun updateGridColumns(columns: Int) {
        viewModelScope.launch { prefsDataStore.updateGridColumns(columns) }
    }

    fun updateShowSuggestions(show: Boolean) {
        viewModelScope.launch { prefsDataStore.updateShowSuggestions(show) }
    }

    fun updateDarkTheme(dark: Boolean) {
        viewModelScope.launch { prefsDataStore.updateDarkTheme(dark) }
    }
}