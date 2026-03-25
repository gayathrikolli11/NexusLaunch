package com.example.nexuslaunch.ui.drawer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Drawer state is owned by HomeViewModel to share search + app list.
// This ViewModel is reserved for future drawer-specific features
// (e.g. pinned apps, folder management).
@HiltViewModel
class DrawerViewModel @Inject constructor() : ViewModel()