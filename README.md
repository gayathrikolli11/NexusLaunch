# NexusLaunch 🚀

A fully functional Android home screen replacement built with system-level Android APIs and Clean Architecture.

## What it does

NexusLaunch replaces your default launcher and intelligently predicts which app you need next using a custom weighted ranking engine — based on how recently, how often, and at what time of day you use each app. The more you use it, the smarter it gets.

## Demo

> Set NexusLaunch as your default home app via Settings → Apps → Default Apps → Home App

## Architecture

Clean Architecture across three layers:
- **Domain** — models, repository interfaces, use cases (pure Kotlin, no Android dependencies)
- **Data** — Room DB, DataStore, repository implementations
- **UI** — Jetpack Compose screens, ViewModels, Hilt injection

## Key Features

- 🏠 **True launcher** — registers as HOME intent, replaces default home screen
- 🤖 **Smart suggestions** — weighted ML ranking engine using recency decay, launch frequency, and time-of-day affinity
- 🔍 **Live search** — instant app filtering in the drawer
- ⚙️ **Settings** — grid columns, dark theme, toggle suggestions on/off
- 📊 **Persistent learning** — Room DB tracks every launch, Flow delivers real-time reactive updates
- 🧪 **Unit tested** — 8 JUnit tests validating the ranking engine edge cases

## Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material Design 3 |
| Architecture | Clean Architecture, MVVM, Repository Pattern |
| DI | Hilt |
| Database | Room + Flow |
| Preferences | DataStore |
| System APIs | LauncherApps, AppWidgetHost, ShortcutManager |
| Testing | JUnit |

## Ranking Engine

`PredictNextAppUseCase` scores every installed app using three signals:
```kotlin
val recencyScore  = exp(-0.1 * hoursAgo)          // exponential decay
val frequencyScore = launchCount / maxLaunchCount   // normalized frequency  
val timeScore     = timeOfDayAffinity()             // morning / work / evening bucket

val totalScore = (recencyScore * 0.5f) + (frequencyScore * 0.3f) + (timeScore * 0.2f)
```

Apps are ranked by `totalScore` in real time and the top 6 surface on the home screen.

## Project Structure
```
com.example.nexuslaunch/
├── data/
│   ├── db/              # Room database, DAO, entities
│   ├── datastore/       # DataStore preferences
│   └── repository/      # Repository implementations
├── domain/
│   ├── model/           # AppInfo, LauncherPreferences
│   ├── repository/      # Repository interfaces
│   └── usecase/         # GetApps, RecordLaunch, PredictNextApp
├── di/                  # Hilt modules
└── ui/
    ├── home/            # HomeScreen + HomeViewModel
    ├── drawer/          # AppDrawerScreen + DrawerViewModel
    ├── component/       # AppIconItem, SuggestedAppsRow
    ├── settings/        # SettingsScreen + SettingsViewModel
    └── theme/           # Material You dynamic theming
```

## Setup

1. Clone the repo
2. Open in Android Studio
3. Run on a device or emulator (API 26+)
4. Go to **Settings → Apps → Default Apps → Home App** and select **NexusLaunch**
5. Press the home button