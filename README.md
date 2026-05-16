# NexusLaunch 🚀

I got tired of my Android launcher showing me Instagram at 6am. So I replaced it.

NexusLaunch is a fully functional home screen replacement built with system-level Android APIs and Clean Architecture. The interesting part is the ranking engine — it learns your daily routine and surfaces the right apps at the right time, without you ever configuring anything.

📝 **I wrote about the algorithm in depth:** [I Got Tired of My Launcher Being Dumb. So I Replaced It.](https://medium.com/@gayathrikolli1905/i-got-tired-of-my-launcher-being-dumb-so-i-replaced-it-97132b63ba05)

---

## Demo

> ⚠️ To try it: go to **Settings → Apps → Default Apps → Home App** and select **NexusLaunch**, then press the home button.

| Home Screen | App Drawer |
|-------------|------------|
| <img width="205" height="454" alt="Screenshot 2026-05-16 161024" src="https://github.com/user-attachments/assets/1d1239da-24f0-4539-be3d-267ae0bcfa18" /> | <img width="203" height="455" alt="Screenshot 2026-05-16 160954" src="https://github.com/user-attachments/assets/030611ba-8192-49c0-9a3c-bc9f2fe6a9c4" /> |

---

## How it works

A simple "most used apps" list isn't smart — it knows how much, but has no idea when. At 7am you want Maps, Slack, Calendar. At 10pm you want Spotify, Netflix. Monday morning looks nothing like Friday night.

`PredictNextAppUseCase` scores every installed app using three signals:

```kotlin
val recencyScore   = exp(-0.1 * hoursAgo)           // exponential decay
val frequencyScore = launchCount / maxLaunchCount    // normalized frequency
val timeScore      = timeOfDayAffinity()             // morning / work / evening bucket

val totalScore = (recencyScore * 0.5f) + (frequencyScore * 0.3f) + (timeScore * 0.2f)
```

The top 6 apps by score surface on the home screen in real time. The engine lives in a standalone `:ranking-engine` Gradle module — zero Android UI dependencies, every test runs on the JVM in milliseconds.

---

## Modules

| Module | Description |
|--------|-------------|
| `:app` | NexusLaunch Android launcher app |
| `:ranking-engine` | Standalone ranking library — see [ranking-engine/README.md](ranking-engine/README.md) |

---

## Key features

- 🏠 **True launcher** — registers as HOME intent, replaces the default home screen
- 🤖 **Smart suggestions** — weighted ranking engine using recency decay, launch frequency, and time-of-day affinity
- 🔍 **Live search** — instant app filtering in the drawer
- ⚙️ **Settings** — grid columns, dark theme, toggle suggestions on/off
- 📊 **Persistent learning** — Room DB tracks every launch, Flow delivers real-time reactive updates
- 🧪 **Unit tested** — JUnit tests validating ranking engine edge cases

---

## Architecture

Clean Architecture across three layers:

- **Domain** — models, repository interfaces, use cases (pure Kotlin, no Android dependencies)
- **Data** — Room DB, DataStore, repository implementations
- **UI** — Jetpack Compose screens, ViewModels, Hilt injection

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

---

## Tech stack

| Category | Technology |
|----------|------------|
| Language | Kotlin |
| UI | Jetpack Compose, Material Design 3 |
| Architecture | Clean Architecture, MVVM, Repository Pattern |
| DI | Hilt |
| Database | Room + Flow |
| Preferences | DataStore |
| System APIs | LauncherApps, AppWidgetHost, ShortcutManager |
| Testing | JUnit |

---

## Setup

1. Clone the repo
2. Open in Android Studio
3. Run on a device or emulator (API 26+)
4. Go to **Settings → Apps → Default Apps → Home App** and select **NexusLaunch**
5. Press the home button

---

## What I'd do differently

- Finer time-of-day buckets — three windows (morning/work/evening) works but loses nuance
- Learn the decay constant per user rather than hardcoding `0.1`
- A/B test the scoring weights with real usage data

---

*Built by [Gayathri Kolli](https://www.linkedin.com/in/gayathri-k-45666a3ab/) · [Read the full writeup](https://medium.com/@gayathrikolli1905/i-got-tired-of-my-launcher-being-dumb-so-i-replaced-it-97132b63ba05)*

