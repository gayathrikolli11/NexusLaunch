package com.example.nexuslaunch.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage")
data class AppUsageEntity(
    @PrimaryKey val packageName: String,
    val launchCount: Int = 0,
    val lastLaunched: Long = 0L
)