package com.example.nexuslaunch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.nexuslaunch.ui.home.HomeScreen
import com.example.nexuslaunch.ui.home.HomeViewModel
import com.example.nexuslaunch.ui.theme.NexusLaunchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsState()

            NexusLaunchTheme(darkTheme = state.preferences.darkTheme) {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}