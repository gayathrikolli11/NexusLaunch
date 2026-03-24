package com.example.nexuslaunch.data.repository

import com.example.nexuslaunch.data.db.AppUsageDao
import com.example.nexuslaunch.domain.model.AppInfo
import com.example.nexuslaunch.domain.repository.UsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: AppUsageDao
) : UsageRepository {

    override fun getUsageStats(limit: Int): Flow<List<AppInfo>> =
        dao.getAllUsage()
            .map { list ->
                list.take(limit).mapNotNull { entity ->
                    val name = runCatching {
                        context.packageManager
                            .getApplicationInfo(entity.packageName, 0)
                            .loadLabel(context.packageManager).toString()
                    }.getOrNull() ?: return@mapNotNull null

                    AppInfo(
                        packageName = entity.packageName,
                        appName = name,
                        icon = runCatching {
                            context.packageManager.getApplicationIcon(entity.packageName)
                        }.getOrNull(),
                        launchCount = entity.launchCount,
                        lastLaunched = entity.lastLaunched
                    )
                }
            }
            .flowOn(Dispatchers.IO)
}