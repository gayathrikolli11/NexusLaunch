package com.example.nexuslaunch.domain.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val launchCount: Int = 0,
    val lastLaunched: Long = 0L,
    val score: Float = 0f   // computed by PredictNextAppUseCase
)