package com.example.nexuslaunch

import com.example.nexuslaunch.domain.model.AppInfo
import com.example.nexuslaunch.domain.usecase.PredictNextAppUseCase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PredictNextAppUseCaseTest {

    private lateinit var useCase: PredictNextAppUseCase

    @Before
    fun setup() {
        useCase = PredictNextAppUseCase()
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun makeApp(
        packageName: String,
        launchCount: Int = 0,
        lastLaunchedMinsAgo: Long = 0L
    ) = AppInfo(
        packageName = packageName,
        appName = packageName,
        icon = null,
        launchCount = launchCount,
        lastLaunched = if (lastLaunchedMinsAgo > 0)
            System.currentTimeMillis() - (lastLaunchedMinsAgo * 60 * 1000)
        else 0L
    )

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    fun `returns empty list when input is empty`() {
        val result = useCase(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `recently launched app scores higher than old app`() {
        val recentApp = makeApp("com.recent", launchCount = 1, lastLaunchedMinsAgo = 5)
        val oldApp = makeApp("com.old", launchCount = 1, lastLaunchedMinsAgo = 600)

        val result = useCase(listOf(oldApp, recentApp))

        assertEquals("com.recent", result.first().packageName)
    }

    @Test
    fun `frequently used app scores higher than rarely used app`() {
        val frequentApp = makeApp("com.frequent", launchCount = 50, lastLaunchedMinsAgo = 120)
        val rareApp = makeApp("com.rare", launchCount = 2, lastLaunchedMinsAgo = 120)

        val result = useCase(listOf(rareApp, frequentApp))

        assertEquals("com.frequent", result.first().packageName)
    }

    @Test
    fun `all apps receive a non-negative score`() {
        val apps = listOf(
            makeApp("com.app1", launchCount = 10, lastLaunchedMinsAgo = 30),
            makeApp("com.app2", launchCount = 0, lastLaunchedMinsAgo = 0),
            makeApp("com.app3", launchCount = 5, lastLaunchedMinsAgo = 200)
        )

        val result = useCase(apps)

        result.forEach { app ->
            assertTrue(
                "Score for ${app.packageName} should be >= 0",
                app.score >= 0f
            )
        }
    }

    @Test
    fun `result is sorted by score descending`() {
        val apps = listOf(
            makeApp("com.app1", launchCount = 1, lastLaunchedMinsAgo = 500),
            makeApp("com.app2", launchCount = 30, lastLaunchedMinsAgo = 10),
            makeApp("com.app3", launchCount = 10, lastLaunchedMinsAgo = 60)
        )

        val result = useCase(apps)

        for (i in 0 until result.size - 1) {
            assertTrue(
                "Item at $i should have score >= item at ${i + 1}",
                result[i].score >= result[i + 1].score
            )
        }
    }

    @Test
    fun `never launched app has zero score`() {
        val neverLaunched = makeApp("com.never", launchCount = 0, lastLaunchedMinsAgo = 0)
        val result = useCase(listOf(neverLaunched))

        assertEquals(0f, result.first().score)
    }

    @Test
    fun `single app is returned as-is with a score`() {
        val app = makeApp("com.solo", launchCount = 5, lastLaunchedMinsAgo = 10)
        val result = useCase(listOf(app))

        assertEquals(1, result.size)
        assertEquals("com.solo", result.first().packageName)
        assertTrue(result.first().score > 0f)
    }

    @Test
    fun `most recently launched app appears in top 3`() {
        val apps = (1..10).map { i ->
            makeApp(
                packageName = "com.app$i",
                launchCount = 10,
                lastLaunchedMinsAgo = (i * 30).toLong()
            )
        }

        val result = useCase(apps)
        val top3Packages = result.take(3).map { it.packageName }

        assertTrue(
            "Most recently launched app should be in top 3",
            top3Packages.contains("com.app1")
        )
    }
}