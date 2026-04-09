package com.nexuslaunch.ranking.model

/**
 * An app with its computed relevance score.
 *
 * @param packageName the app's package name
 * @param score relevance score between 0.0 and 1.0. Higher = more relevant right now.
 */
data class RankedApp(
    val packageName: String,
    val score: Float
)