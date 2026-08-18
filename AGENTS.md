# AGENTS.md

## Overview

Doubean is an unofficial Android app for Douban (Groups, Books, Movies, TVs).

## Tech Stack

- **UI**: Kotlin, Compose Material 3, Navigation 3, Coil
- **Architecture**: Pragmatic Vertical Slicing + Shared Core, MVVM (ViewModel, Flow, Paging)
- **Data**: Room, DataStore, Ktor, Kotlinx Serialization
- **DI**: Hilt
- **Other**: libsu (Root)

## Structure
Source: `app/src/main/java/com/github/bumblebee202111/doubean`

- `feature/`: Vertically sliced features. Each owns its specific `data` (Repositories, Mappers,
  Paging) and `ui` (Screens, ViewModels).
- `data/`: Shared data layer. Contains global Room setup (`db`), DataStore (`prefs`), and
  cross-feature Repositories (`repository/`).
- `network/`: Anti-Corruption Layer acting as the "Douban SDK". Mirrors Douban's decompiled backend
  models. **Do not vertically slice.**
- `model/`: Shared domain models (ubiquitous language).
- `ui/`: Generic, domain-agnostic UI components and theme.
- `navigation/`, `security/`, `coroutines/`, `util/`: Top-level foundational infrastructure.

## Workflow
- **Build**: `./gradlew assembleDebug`
- **Test**: `./gradlew test`
- **Env**: JDK 17+, Android SDK 35

## Guidelines

- **Architecture**: Features are isolated. If a Repository is used by >1 feature, place it in
  `data/repository/`. Otherwise, keep it in `feature/<name>/data/`.
- **UI**: Compose only. Use `DoubeanTheme`. We use a slightly refined Material 3 style (crisper,
  less bubbly).
- **Navigation**: Navigation 3. Use `@Serializable NavKey` for routes. Manage routing via `Navigator` and `NavDisplay`. Deep links are parsed manually into keys.
- **Net**: Ktor. Errors via `SnackbarManager`.
- **Naming**: Match API or decompiled field names where applicable.
- **Root**: Optional via `libsu`.
- **Deps**: `gradle/libs.versions.toml`.