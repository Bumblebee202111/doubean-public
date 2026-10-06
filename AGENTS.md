# AGENTS.md

## Overview

Doubean is an unofficial Android app for Douban (Groups, Books, Movies, TVs).

## Tech Stack

- **UI**: Kotlin, Compose Material 3, Navigation 3, Coil
- **Architecture**: In flux (Vertical Slicing + Layered Data/Core), MVVM (ViewModel, Flow, Paging)
- **Data**: Room, DataStore, Ktor, Kotlinx Serialization
- **DI**: Hilt
- **Other**: libsu (Root)

## Structure
Source: `app/src/main/java/com/github/bumblebee202111/doubean`

- `feature/`: Feature presentation (Screens, ViewModels, UI states, NavKeys). Isolated; no cross-feature imports.
- `data/`: Data layer. Room (`db`), DataStore (`prefs`), and domain repositories/mappers (`data/<domain>/`).
- `core/network/`: Internal "Douban SDK". Mirrors decompiled Douban APIs and DTOs for easy cross-referencing; do not vertically slice.
- `core/`: Technical infrastructure (`core/common`, `core/theme`).
- `model/`: Shared domain entities (`model/<domain>/`, singular).
- `ui/`: Shared UI components, theme, and common presentation delegates.
- `navigation/`, `security/`, `coroutines/`, `util/`: Top-level infrastructure.

## Workflow
- **Env**: JDK 17+, Android SDK 35

## Guidelines

- **Architecture**: Features are isolated. Shared domain models belong in `model/<domain>/`. Repositories live in `data/<domain>/` (kept feature-local only if strictly screen-private).
- **UI**: Compose only. Use `DoubeanTheme`. We use a slightly refined Material 3 style (crisper,
  less bubbly).
- **Navigation**: Navigation 3. Use `@Serializable NavKey` for routes. Manage routing via `Navigator` and `NavDisplay`. Deep links are parsed manually into keys.
- **Net**: Ktor. Errors via `SnackbarManager`.
- **Naming**: Match API or decompiled field names where applicable.
- **Root**: Optional via `libsu`.
- **Deps**: `gradle/libs.versions.toml`.