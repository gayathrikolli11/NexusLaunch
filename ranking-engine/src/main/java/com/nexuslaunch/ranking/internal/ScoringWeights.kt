package com.nexuslaunch.ranking.internal

internal object ScoringWeights {
    const val RECENCY = 0.5f
    const val FREQUENCY = 0.3f
    const val TIME_OF_DAY = 0.2f
    const val RECENCY_DECAY = 0.1
}