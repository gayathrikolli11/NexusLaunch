package com.example.nexuslaunch.domain.model

data class LauncherPreferences(
    val gridColumns: Int = 4,
    val showSuggestedApps: Boolean = true,
    val darkTheme: Boolean = false
)