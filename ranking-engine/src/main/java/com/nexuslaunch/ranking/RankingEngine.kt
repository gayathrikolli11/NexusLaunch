package com.nexuslaunch.ranking

import com.nexuslaunch.ranking.internal.PredictNextAppUseCase
import com.nexuslaunch.ranking.model.AppUsageData
import com.nexuslaunch.ranking.model.RankedApp

/**
 * NexusLaunch Ranking Engine
 *
 * Ranks apps by predicted relevance using a weighted scoring model
 * combining recency, frequency, and time-of-day signals.
 *
 * Usage:
 * ```
 * val engine = RankingEngine()
 * val ranked = engine.rank(appUsageList)
 * val top6 = ranked.take(6)
 * ```
 */
class RankingEngine {

    private val useCase = PredictNextAppUseCase()

    /**
     * Ranks a list of apps by predicted relevance.
     *
     * @param apps list of [AppUsageData] — package name + launch stats
     * @param limit max number of results to return. 0 = return all.
     * @return list of [RankedApp] sorted by score descending
     */
    fun rank(apps: List<AppUsageData>, limit: Int = 0): List<RankedApp> {
        val ranked = useCase.invoke(apps)
        return if (limit > 0) ranked.take(limit) else ranked
    }
}