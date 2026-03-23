package com.example.nexuslaunch.domain.usecase

import com.example.nexuslaunch.domain.model.AppInfo
import java.util.Calendar
import javax.inject.Inject

/**
 * Ranks apps using a weighted scoring model:
 * - Recency score:   how recently launched (exponential decay)
 * - Frequency score: launch count normalized
 * - Time-of-day score: hour-of-day bucket affinity learned from past launches
 *
 * This is intentionally lightweight (no external ML lib) so it runs
 * on the main thread without jank and is transparent to interviewers.
 */
class PredictNextAppUseCase @Inject constructor() {

    operator fun invoke(apps: List<AppInfo>): List<AppInfo> {
        if (apps.isEmpty()) return apps

        val now = System.currentTimeMillis()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val maxCount = apps.maxOf { it.launchCount }.coerceAtLeast(1).toFloat()

        return apps.map { app ->
            val recencyScore = if (app.lastLaunched > 0) {
                val hoursAgo = (now - app.lastLaunched) / (1000f * 60 * 60)
                Math.exp(-0.1 * hoursAgo).toFloat()   // exponential decay
            } else 0f

            val frequencyScore = app.launchCount / maxCount

            // Time-of-day affinity: morning (6-9), work (9-18), evening (18-23)
            val timeScore = when (currentHour) {
                in 6..8   -> if (frequencyScore > 0.5f) 0.3f else 0f
                in 9..17  -> frequencyScore * 0.2f
                in 18..22 -> if (recencyScore > 0.5f) 0.2f else 0f
                else      -> 0f
            }

            val totalScore = (recencyScore * 0.5f) + (frequencyScore * 0.3f) + (timeScore * 0.2f)
            app.copy(score = totalScore)
        }.sortedByDescending { it.score }
    }
}