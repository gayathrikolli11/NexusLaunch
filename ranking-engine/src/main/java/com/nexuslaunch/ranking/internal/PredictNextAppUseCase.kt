package com.nexuslaunch.ranking.internal

import com.nexuslaunch.ranking.model.AppUsageData
import com.nexuslaunch.ranking.model.RankedApp
import java.util.Calendar
import kotlin.math.exp

internal class PredictNextAppUseCase {

    fun invoke(apps: List<AppUsageData>): List<RankedApp> {
        if (apps.isEmpty()) return emptyList()

        val now = System.currentTimeMillis()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val maxCount = apps.maxOf { it.launchCount }.coerceAtLeast(1).toFloat()

        return apps.map { app ->
            val recencyScore = if (app.lastLaunchedMs > 0) {
                val hoursAgo = (now - app.lastLaunchedMs) / (1000f * 60 * 60)
                exp(-ScoringWeights.RECENCY_DECAY * hoursAgo).toFloat()
            } else 0f

            val frequencyScore = app.launchCount / maxCount

            val timeScore = when (currentHour) {
                in 6..8 -> if (frequencyScore > 0.5f) 0.3f else 0f
                in 9..17 -> frequencyScore * 0.2f
                in 18..22 -> if (recencyScore > 0.5f) 0.2f else 0f
                else -> 0f
            }

            val total = (recencyScore * ScoringWeights.RECENCY) +
                    (frequencyScore * ScoringWeights.FREQUENCY) +
                    (timeScore * ScoringWeights.TIME_OF_DAY)

            RankedApp(packageName = app.packageName, score = total)
        }.sortedByDescending { it.score }
    }
}