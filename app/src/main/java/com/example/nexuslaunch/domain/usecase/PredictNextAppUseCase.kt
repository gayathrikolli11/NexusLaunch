package com.example.nexuslaunch.domain.usecase

import com.example.nexuslaunch.domain.model.AppInfo
import com.nexuslaunch.ranking.RankingEngine
import com.nexuslaunch.ranking.model.AppUsageData
import javax.inject.Inject

class PredictNextAppUseCase @Inject constructor() {

    private val engine = RankingEngine()

    operator fun invoke(apps: List<AppInfo>): List<AppInfo> {
        val usageData = apps.map {
            AppUsageData(
                packageName = it.packageName,
                launchCount = it.launchCount,
                lastLaunchedMs = it.lastLaunched
            )
        }
        val ranked = engine.rank(usageData)
        val scoreMap = ranked.associate { it.packageName to it.score }
        return apps
            .map { it.copy(score = scoreMap[it.packageName] ?: 0f) }
            .sortedByDescending { it.score }
    }
}