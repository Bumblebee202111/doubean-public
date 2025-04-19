doubean
=======

An unofficial [Douban][douban] app for browsing [Groups][douban-groups] and exploring Subjects  (books/movies/TVs)
\([豆瓣][douban]非官方app，支持[小组][douban-groups]浏览和书影~~音~~探索\)

**Note**:  Experimental project focusing on core features while testing Jetpack Compose. Occasional crashes, incomplete UI designs, or missing features may occur.

**Known Issues**:

- Login (image) captcha: Needs more testing

[douban]: https://www.douban.com/

[douban-groups]: https://www.douban.com/group/

Introduction
------------

### Screens

#### Statuses

- Statuses feed

#### Subjects

- **Home**: Personal statuses + subject unions + rank lists
- **Interests**: Tracked subject statuses
- **Search**: Subject discovery
- **Details**: Metadata/intro/interactions
- **Rank List**

#### Groups

- **Home**: Favorites/subscriptions
- **Detail**: Group info + tabs
- **Topics**: Content + comments
- **Search**: Group discovery
- **Notifications**: Topic updates

#### Shared

- Image Viewer
- Profile & Login

### Screenshots

<p> <img src="screenshots/tablet_movie.png" alt="tablet_movie" height="450" />   
    <img src="screenshots/phone_subjects.png" alt="phone_subjects" height="300" />
<img src="screenshots/tablet_interests.png" alt="tablet_interests" height="450" />   
<img src="screenshots/phone_groups_home.png" alt="phone_groups" height="300" />
<img src="screenshots/phone_group_detail.png" alt="phone_group_detail" height="300" />
<img src="screenshots/phone_post_detail.png" alt="phone_post_detail" height="300" /></p>


## Features

* Subjects: Basic interactions (WIP 🚧)
* Group enhancements:
  * Locally favorite tabs/groups 
  * Keep original list orders or sort by create time within any page
  * Customizable topic notifications
* Ad-free and lightweight (~3MB)
* Auth: Session reuse (root) + login (beta)
* Partial URL deep linking support
* Direct official API calls
* Basic MD3 implementation
* Statuses (legacy)
* Mobile-optimized pure Web screens (deprecated)

## Getting Started

- [Latest release][latest-release]
- Requirements: Android: 8.1+
- Feel free to file issues
  - Include details if data fails to display (common causes: JSON parsing errors):
    - Titles and related URLs (from Douban app)
    - Raw JSON (if accessible).
- Keep the official Douban app installed:
  - Support Douban's service  
  - Access unsupported content 
  - Root users: Reuse login sessions
- Clear app data after updates if crashes occur

[latest-release]: https://github.com/Bumblebee202111/doubean-public/releases/latest

## Important Notices

### Limited Open-Source

To avoid potential issues, public repo retains an **outdated codebase**. Non-sensitive snippets may be shared upon request.

### Guidelines

- **Do:** Use for personal learning.
- **Don’t:** Promote elsewhere without permission.

## Tech Stack

* [Foundation][foundation]: [Android KTX][android-ktx]
* [Architecture][arch]: [Lifecycles][lifecycle], [Navigation][navigation], [Paging][paging], [Room][room], DataStore, [ViewModel][viewmodel], [WorkManager][workmanager]
* [UI][ui]: Jetpack Compose
* Behavior: [Notifications][notifications]
* Third party libraries
  * [Kotlin Coroutines][kotlin-coroutines]
  * Kotlinx Serialization
  * Ktor
  * Coil
  * libsu
  * Accompanist Permissions/WebView

[foundation]: https://developer.android.com/jetpack/components

[appcompat]: https://developer.android.com/topic/libraries/support-library/packages#v7-appcompat

[android-ktx]: https://developer.android.com/kotlin/ktx

[test]: https://developer.android.com/training/testing/

[arch]: https://developer.android.com/jetpack/arch/

[lifecycle]: https://developer.android.com/topic/libraries/architecture/lifecycle

[navigation]: https://developer.android.com/topic/libraries/architecture/navigation/

[paging]: https://developer.android.com/topic/libraries/architecture/paging/v3-overview

[room]: https://developer.android.com/topic/libraries/architecture/room

[viewmodel]: https://developer.android.com/topic/libraries/architecture/viewmodel

[workmanager]:https://developer.android.com/topic/libraries/architecture/workmanager

[ui]: https://developer.android.com/guide/topics/ui

[notifications]: https://developer.android.com/develop/ui/views/notifications

[kotlin-coroutines]: https://kotlinlang.org/docs/reference/coroutines-overview.html

## Roadmap

Incoming features, bug fixes, libraries to use and environment changes \(roughly in chronological order\). Primarily for myself.

### Current release \(0.8.4)

### Next release \(0.8.5)


### Future plans

* More error handling

  * Process error body
  * Show error messages

* Last-active tab persistence

* Groups
  * Dynamic topic sorts-by

  - Simplify topic caching
  
  * Home
    * Maybe support pagination of recommended topics
  * Subscribe/favorite/save
    * Remove support for locally favoriting groups (subscribing groups already available)
    * Save topics
    * Pin my groups
    * Add corresponding item actions
  * Group tab: Track topic read status
  * Search
    * Search topics within a specific group/tab
    * Global topic search across all groups
    * Result tabs
  * Group Detail
  
    * Collapse on entrance for the subscribed/favorited group/tab
    * Fix group description action overflows
  * Fix topic content losing scroll position after navigating back
  * Notifications
    * Custom interval. Support options of 15m/1h 
  * Hide officially-flagged unfriendly content by default
  * Support blocking unwanted content
  * Lists: Reddit-style item expand/collapse
  * Find API for all followed topics (currently unlikely)
  
* Dou Lists

* Sync certain files to public repo with a script

* Expand/collapse component: Hide action for short text

* Search suggestions

* Profile/User

* Subjects

  * Color schemes
  * More

* Login

  * Code login (w/o pw)

* Update logo: Swap bg/fg colors

* Shortcuts

* Properly show login prompts & normal tooltips/info

* Load state visualization (removed during Compose migrations for simplicity)
  * Paging 3 refresh, loading status ...

* Restore dark mode & landscape support

* ImageScreen -> Shared element transitions

* List item menus → dialog sheets

* Improve model layering

* Languages
  * Translations
  * Pick language

* NetworkManager

* Widgets

* Optionally sync more official app prefs for API request consistency for rooted users

* Consider use single NavHost

* Statuses (maintenance mode)
  * Additional card types
  * Pagination

* Test

## References

* Jetpack Compose samples
* [Android Sunflower][sunflower]
* [Developer Guides][guides]
* [Github Browser Sample with Android Architecture Components][github-browser-sample]
* [Android Architecture Blueprints v1 (todo-mvvm-live)][todo-mvvm-live]
* [Material Design][material]
* Various social apps: used as references for UI design and functionality

[sunflower]: https://github.com/android/sunflower

[guides]: https://developer.android.google.cn/guide

[github-browser-sample]: https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample

[todo-mvvm-live]: https://github.com/android/architecture-samples/tree/todo-mvvm-live

[material]:https://material.io/

## Utilities

* [HTTPCanary][http-canary] for analyzing Douban API requests
* [jadx][jadx]-gui for understanding models
* [Google Chrome][google-chrome] for debugging CSS

[http-canary]:https://github.com/MegatronKing/HttpCanary/

[jadx]:https://github.com/skylot/jadx/releases

[google-chrome]:https://www.google.com/chrome/

## Stars :star: & Donation :coffee:

I know this app isn’t perfect, but your stars keeps me motivated. Thank you!

To support development (though not actively seeking donations), contact me via issues. 

