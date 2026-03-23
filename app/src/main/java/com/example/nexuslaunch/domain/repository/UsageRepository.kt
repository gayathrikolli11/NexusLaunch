package com.example.nexuslaunch.domain.repository

import com.example.nexuslaunch.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface UsageRepository {
    fun getUsageStats(limit: Int = 8): Flow<List<AppInfo>>
}