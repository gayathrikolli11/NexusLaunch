package com.example.nexuslaunch.domain.repository

import com.example.nexuslaunch.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getInstalledApps(): Flow<List<AppInfo>>
    suspend fun recordLaunch(packageName: String)
    suspend fun getLaunchStats(packageName: String): Pair<Int, Long>
}