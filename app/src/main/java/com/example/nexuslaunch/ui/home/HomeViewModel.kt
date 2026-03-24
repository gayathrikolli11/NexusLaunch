package com.example.nexuslaunch.ui.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexuslaunch.data.datastore.PreferencesDataStore
import com.example.nexuslaunch.domain.model.AppInfo
import com.example.nexuslaunch.domain.model.LauncherPreferences
import com.example.nexuslaunch.domain.usecase.GetAppsUseCase
import com.example.nexuslaunch.domain.usecase.PredictNextAppUseCase
import com.example.nexuslaunch.domain.usecase.RecordAppLaunchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val suggestedApps: List<AppInfo> = emptyList(),
    val allApps: List<AppInfo> = emptyList(),
    val preferences: LauncherPreferences = LauncherPreferences(),
    val isLoading: Boolean = true,
    val isDrawerOpen: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAppsUseCase: GetAppsUseCase,
    private val predictNextAppUseCase: PredictNextAppUseCase,
    private val recordAppLaunchUseCase: RecordAppLaunchUseCase,
    private val prefsDataStore: PreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeApps()
        observePreferences()
    }

    private fun observeApps() {
        viewModelScope.launch {
            getAppsUseCase()
                .map { apps ->
                    val ranked = predictNextAppUseCase(apps)
                    Pair(ranked.take(6), apps)
                }
                .collect { (suggested, all) ->
                    _uiState.update {
                        it.copy(
                            suggestedApps = suggested,
                            allApps = all,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            prefsDataStore.preferences.collect { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
    }

    fun openAppInfo(packageName: String) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun launchApp(appInfo: AppInfo) {
        viewModelScope.launch {
            recordAppLaunchUseCase(appInfo.packageName)
        }
        val intent = context.packageManager
            .getLaunchIntentForPackage(appInfo.packageName)
            ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        intent?.let { context.startActivity(it) }
    }

    fun toggleDrawer() {
        _uiState.update { it.copy(isDrawerOpen = !it.isDrawerOpen) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun getFilteredApps(): List<AppInfo> {
        val q = _uiState.value.searchQuery.trim().lowercase()
        return if (q.isEmpty()) _uiState.value.allApps
        else _uiState.value.allApps.filter { it.appName.lowercase().contains(q) }
    }
}