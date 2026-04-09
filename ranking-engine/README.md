# NexusLaunch Ranking Engine

A standalone Android library that predicts which app a user needs
next based on their usage patterns.

## How it works

Scores every app using three weighted signals:
- **Recency (50%)** — exponential decay based on last launch time
- **Frequency (30%)** — normalized launch count
- **Time of day (20%)** — usage affinity by hour bucket

## Installation

In your `settings.gradle.kts`:
```kotlin
include(":ranking-engine")
project(":ranking-engine").projectDir = File("path/to/ranking-engine")
```

In your `build.gradle.kts`:
```kotlin
implementation(project(":ranking-engine"))
```

## Usage

```kotlin
val engine = RankingEngine()

val apps = listOf(
    AppUsageData("com.google.android.gm", launchCount = 42, lastLaunchedMs = System.currentTimeMillis() - 3600000),
    AppUsageData("com.spotify.music", launchCount = 80, lastLaunchedMs = System.currentTimeMillis() - 300000),
    AppUsageData("com.netflix.mediaclient", launchCount = 10, lastLaunchedMs = 0L)
)

val top3 = engine.rank(apps, limit = 3)
// Returns ranked list sorted by predicted relevance
```

## Public API

| Class | Visibility | Purpose |
|---|---|---|
| `RankingEngine` | public | Entry point — call `rank()` |
| `AppUsageData` | public | Input model |
| `RankedApp` | public | Output model |
| `PredictNextAppUseCase` | internal | Scoring logic, hidden |
| `ScoringWeights` | internal | Weight constants, hidden |