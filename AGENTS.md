# AGENTS.md

## Overview

Doubean is an unofficial Android app for [Douban](https://www.douban.com/) (Groups, Books, Movies, TVs), built with
Jetpack Compose.

## Tech Stack

- **Kotlin** & **Compose** (Material 3)
- **Architecture**: MVVM (ViewModel, Flow, Navigation, Paging, WorkManager)
- **Data**: Room, DataStore, Ktor, Serialization
- **DI**: Hilt
- **Image**: Coil
- **Other**: libsu (Root), Accompanist

## Structure

Source: `app/src/main/java/com/github/bumblebee202111/doubean`

- `ui`: Screens, components (`common`, `component`), and `theme`.
- `data`: Repositories (`repository`), Room (`db`), DataStore (`prefs`).
- `model`: Entities (`auth`, `groups`, `subjects`, `statuses`, `doulists`).
- `network`: Ktor APIs (`ApiService`, `DoubanWebService`).
- `feature`:
    - `app`: Main navigation shell.
    - `groups`: Discussions and topics.
    - `subjects`: Books, Movies, TVs.
    - `statuses`: User timeline.
    - `doulists`: Public collections.
    - `mydoulists`: User's collections.
    - `search`: Search.
  - `imageviewer`: Image viewer.
    - `login`: Authentication.
    - `settings`: Preferences.
    - `notifications`: Notifications screen.
    - `userprofile`: Profile.
- `navigation`: Nav graphs (`MainNavHost`, `BottomNavHost`).
- `domain`: Domain logic (`ObserveCurrentUserUseCase`).
- `util`: Utilities (`NetworkMonitor`, `ShareUtil`).
- `workers`: WorkManager (`TopicNotificationsWorker`).
- `notifications`: Notification manager (`Notifier`).
- `security`: Cryptography.
- `coroutines`: Dispatchers.

## Workflow

- **Build**: `./gradlew assembleDebug`
- **Test**: `./gradlew test`
- **Env**: JDK 17+, Android SDK 34.

## Guidelines

- **UI**: Compose only. Use `DoubeanTheme`.
- **Arch**: MVVM. State via Flows.
- **Net**: Ktor. Errors via `SnackbarManager`.
- **Naming**: Match API or decompiled field names where applicable.
- **Root**: Optional via `libsu`.
- **Deps**: `gradle/libs.versions.toml`.
