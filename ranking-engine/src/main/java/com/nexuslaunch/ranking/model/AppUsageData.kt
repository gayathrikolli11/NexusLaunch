package com.nexuslaunch.ranking.model

/**
 * Represents a single app's usage data.
 * This is the only input the ranking engine needs.
 *
 * @param packageName unique identifier e.g. "com.google.android.gm"
 * @param launchCount total number of times this app has been launched
 * @param lastLaunchedMs epoch timestamp in milliseconds of last launch, 0 if never launched
 */
data class AppUsageData(
    val packageName: String,
    val launchCount: Int = 0,
    val lastLaunchedMs: Long = 0L
)