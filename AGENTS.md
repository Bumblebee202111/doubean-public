# AGENTS.md

## Overview

Doubean is an unofficial Android app for [Douban](https://www.douban.com/) (Groups, Books, Movies, TVs), built with
Jetpack Compose.

## Tech Stack

- **Kotlin** & **Compose** (Material 3)
- **Architecture**: MVVM (ViewModel, Flow, Navigation 3, Paging, WorkManager)
- **Data**: Room, DataStore, Ktor, Serialization
- **DI**: Hilt
- **Image**: Coil
- **Other**: libsu (Root), Accompanist

## Structure

Source: `app/src/main/java/com/github/bumblebee202111/doubean`

- `ui`: Main app shell (`DoubeanApp`, `MainNavScreen`), Screens, components (`common`, `component`), and `theme`.
- `data`: Repositories (`repository`), Room (`db`), DataStore (`prefs`).
- `model`: Entities (`auth`, `groups`, `subjects`, `statuses`, `doulists`).
- `network`: Ktor APIs (`ApiService`, `DoubanWebService`).
- `feature`:
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
- `navigation`: Navigation 3 state and routing (`Navigator`, `NavigationState`, `SceneStrategy`, `EntryProvider`, `DoubeanDeepLinks`).
- `domain`: Domain logic (`ObserveCurrentUserUseCase`).
- `util`: Utilities (`NetworkMonitor`, `ShareUtil`).
- `workers`: WorkManager (`TopicNotificationsWorker`).
- `notifications`: Notification manager (`Notifier`).
- `security`: Cryptography.
- `coroutines`: Dispatchers.

## Workflow

- **Build**: `./gradlew assembleDebug`
- **Test**: `./gradlew test`
- **Env**: JDK 17+, Android SDK 35.

## Guidelines

- **UI**: Compose only. Use `DoubeanTheme`.
- **Arch**: MVVM. State via Flows.
- **Navigation**: Navigation 3. Use `@Serializable NavKey` for routes. Manage routing via `Navigator` and `NavDisplay`. Deep links are parsed manually into keys.
- **Net**: Ktor. Errors via `SnackbarManager`.
- **Naming**: Match API or decompiled field names where applicable.
- **Root**: Optional via `libsu`.
- **Deps**: `gradle/libs.versions.toml`.
