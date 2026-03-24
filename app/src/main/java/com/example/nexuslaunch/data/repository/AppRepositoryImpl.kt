package com.example.nexuslaunch.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.example.nexuslaunch.data.db.AppUsageDao
import com.example.nexuslaunch.data.db.AppUsageEntity
import com.example.nexuslaunch.domain.model.AppInfo
import com.example.nexuslaunch.domain.repository.AppRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: AppUsageDao
) : AppRepository {

    override fun getInstalledApps(): Flow<List<AppInfo>> =
        dao.getAllUsage()
            .map { usageList ->
                val usageMap = usageList.associateBy { it.packageName }
                queryLauncherApps()
                    .map { (pkg, name) ->
                        val usage = usageMap[pkg]
                        AppInfo(
                            packageName = pkg,
                            appName = name,
                            icon = runCatching {
                                context.packageManager.getApplicationIcon(pkg)
                            }.getOrNull(),
                            launchCount = usage?.launchCount ?: 0,
                            lastLaunched = usage?.lastLaunched ?: 0L
                        )
                    }
                    .sortedBy { it.appName }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun recordLaunch(packageName: String) {
        val now = System.currentTimeMillis()
        val existing = dao.getUsage(packageName)
        if (existing == null) {
            dao.upsert(AppUsageEntity(packageName, launchCount = 1, lastLaunched = now))
        } else {
            dao.incrementLaunch(packageName, now)
        }
    }

    override suspend fun getLaunchStats(packageName: String): Pair<Int, Long> {
        val entity = dao.getUsage(packageName)
        return Pair(entity?.launchCount ?: 0, entity?.lastLaunched ?: 0L)
    }

    private fun queryLauncherApps(): List<Pair<String, String>> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            PackageManager.ResolveInfoFlags.of(0L) else null

        val resolveList = if (flags != null)
            context.packageManager.queryIntentActivities(intent, flags)
        else
            @Suppress("DEPRECATION")
            context.packageManager.queryIntentActivities(intent, 0)

        return resolveList
            .filter { it.activityInfo.packageName != context.packageName }
            .map { info ->
                val pkg = info.activityInfo.packageName
                val name = info.loadLabel(context.packageManager).toString()
                pkg to name
            }
            .distinctBy { it.first }
    }
}