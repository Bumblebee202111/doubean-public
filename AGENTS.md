# AGENTS.md

## Overview

Doubean is an unofficial Android app for [Douban](https://www.douban.com/) (Groups, Books, Movies,
TVs), built with Jetpack Compose.

## Tech Stack
- **Kotlin** & **Compose** (Material 3)
- **Architecture**: Pragmatic Clean Architecture (Vertical Slicing), MVVM (ViewModel, Flow,
  Navigation 3, Paging)
- **Data**: Room, DataStore, Ktor, Serialization
- **DI**: Hilt
- **Image**: Coil
- **Other**: libsu (Root), Accompanist

## Structure
Source: `app/src/main/java/com/github/bumblebee202111/doubean`

- `feature`: Vertically sliced features (`groups`, `subjects`, `statuses`, `doulists`, `search`,
  `imageviewer`, `login`, `settings`, `userprofile`). Each feature owns its specific `data` (
  Repositories), `domain` (Logic), and `ui` (Screens, ViewModels, and Domain-aware components).
- `data`: Globally shared data sources (Room `db`, DataStore `prefs`, and shared repos like
  `AuthRepository`).
- `network`: Ktor APIs and DTOs (Anti-Corruption Layer matching Douban's backend).
- `model`: Shared domain models.
- `ui`: Generic, domain-agnostic UI components and theme.
- `navigation`: Navigation 3 state and routing.
- `security`, `coroutines`, `util`: Top-level foundational infrastructure.

## Workflow
- **Build**: `./gradlew assembleDebug`
- **Test**: `./gradlew test`
- **Env**: JDK 17+, Android SDK 35.

## Guidelines

- **Architecture**: We use Vertical Slicing. Place feature-specific Repositories, Workers, and
  domain-aware UI components inside their respective `feature/<name>/` packages. Do not create
  monolithic `core` or `data` dumping grounds.
- **UI**: Compose only. Use `DoubeanTheme`. We use a slightly refined Material 3 style (crisper,
  less bubbly).
- **Navigation**: Navigation 3. Use `@Serializable NavKey` for routes. Manage routing via `Navigator` and `NavDisplay`. Deep links are parsed manually into keys.
- **Net**: Ktor. Errors via `SnackbarManager`.
- **Naming**: Match API or decompiled field names where applicable.
- **Root**: Optional via `libsu`.
- **Deps**: `gradle/libs.versions.toml`.
