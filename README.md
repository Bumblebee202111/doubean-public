doubean
=======

An unofficial [Douban][douban] app for browsing [Groups][douban-groups] and exploring Subjects (books/movies/TVs)
\([豆瓣][douban]非官方app，支持[小组][douban-groups]浏览和书影~~音~~探索\)

**Note**: This is an experimental project focusing on core features while testing Jetpack Compose. Occasional crashes, incomplete UI designs, or missing features may occur.

**Known Issues**:

*   Login (image) captcha: Needs further testing.

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
- **Details**: Metadata / intro / interactions
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

- Subjects: Basic interactions (WIP 🚧)
- Group enhancements:
  - Locally favorite tabs/groups
  - Keep original list orders or sort by create time within any page
  - Customizable topic notifications
- Ad-free and lightweight (~3MB)
- Auth: Session reuse (root required) + login (beta)
- Partial URL deep linking support
- Direct official API calls
- Basic MD3 implementation
- Statuses (legacy)
- Mobile-optimized pure Web screens (deprecated)

## Getting Started

- [Latest release][latest-release]
- Requirements: Android: 8.1+
- Feel free to file issues
  - If data fails to display, please include:
    - Titles
    - Error messages (if any are shown)
    - Raw JSON (if accessible)
- Keep the official Douban app installed to:
  - Support Douban services 
  - Access content not supported by this app
  - Allow root users to reuse login sessions
- Clear app data after updates if crashes occur

[latest-release]: https://github.com/Bumblebee202111/doubean-public/releases/latest

## Important Notices

### Source Code Availability

Only selected source code from the private project is shared to mitigate potential issues. (WIP)

### Guidelines

- **Do:** Use for personal learning.
- **Don’t:** Promote elsewhere without permission.

## Tech Stack

- [Foundation][foundation]: [Android KTX][android-ktx]
- [Architecture][arch]: [Lifecycles][lifecycle], [Navigation][navigation], [Paging][paging], [Room][room], DataStore, [ViewModel][viewmodel], [WorkManager][workmanager]
- [UI][ui]: Jetpack Compose
- Behavior: [Notifications][notifications]
- Third-party libraries
  - [Kotlin Coroutines][kotlin-coroutines]
  - Kotlinx Serialization
  - Ktor
  - Coil
  - libsu
  - Accompanist Permissions/WebView

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

Upcoming features, bug fixes, libraries to use and environment changes \(roughly in chronological order\). Primarily for personal tracking.

### Current release \(0.8.5)

- Error handling
- Profile
  - My subject counts
- Dep
- Fix group sub button not immediately updated

### Next release \(0.9.0)

- Update base Douban version
- Profile/User
  - Dou lists
  - Click avatar/name to view
- Dou lists


### Future plans

- Groups 
  - Subscribe/favorite/collect/react
    - Collect topics
    - Remove support for locally favoriting groups (use official subscribing)
    - Pin my groups
    - Add corresponding item actions
  - Tab/tag → 分区
  - Notifications: Custom interval (e.g., 15m/1h options)
  - Search:
    - Search topics within a specific group/tab
    - Global topic search across all groups
    - Tabbed search results
  - Dynamic topic sorting options
  - Simplify topic caching
  - Group Home: Potentially add pagination for recommended topics
  - Group tab: Track topic read status
  - Group Detail
    - Auto-collapse header on entry for subscribed/favorited groups/tabs
    - Fix group description action overflows
  - Topic:
    - Fully load with official script, no manual parsing
    - Fix content scroll position loss after navigating back
  - Hide officially-flagged "unfriendly" content by default
  - Implement content blocking
  - Lists: Reddit-style item expand/collapse toggle
  - Find API for *all* followed topics like desktop Web app (currently seems unlikely)
    - Use as home/notifications data source
- Subjects
  - Subject Detail: Ratings histogram
  - Color schemes
  - More
- Sync more source files to public repo
- Last-active tab persistence
- Complete deep linking support
- Expand/collapse component: Hide action for short text
- Search suggestions
- Login
  - Code-based (w/o pw)
- Update app icon: Swap bg/fg colors
- Shortcuts
- Improve display of login prompts and standard tooltips/info messages
- Restore load state visualization (removed during Compose migration)
  - Paging 3 refresh, loading status ...
- Restore dark mode & landscape support
- ImageScreen → Shared element transitions
- List item menus → Bottom dialog sheets
- Improve model layering
- i18n
  - Translations
  - Language selection
- NetworkManager
- Widgets
- QR
- (Rooted users) Optionally sync more official app preferences for API request consistency
- Consider use single NavHost
- Statuses (maintenance mode)
  - Additional card types
  - Pagination

* Test

## References

- Jetpack Compose samples
- [Android Sunflower][sunflower]
- [Developer Guides][guides]
- [Github Browser Sample with Android Architecture Components][github-browser-sample]
- [Android Architecture Blueprints v1 (todo-mvvm-live)][todo-mvvm-live]
- [Material Design][material]
- Various social apps (UI/UX inspiration)

[sunflower]: https://github.com/android/sunflower
[guides]: https://developer.android.google.cn/guide
[github-browser-sample]: https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample
[todo-mvvm-live]: https://github.com/android/architecture-samples/tree/todo-mvvm-live
[material]:https://material.io/

## Utilities

- [HTTPCanary][http-canary] for analyzing Douban API requests
- [jadx][jadx]-gui for understanding models
- [Google Chrome][google-chrome] for debugging CSS

[http-canary]:https://github.com/MegatronKing/HttpCanary/

[jadx]:https://github.com/skylot/jadx/releases

[google-chrome]:https://www.google.com/chrome/

## Stars :star: & Donation :coffee:

Your ⭐ **stars** motivate continued development!
While not actively seeking donations, if you wish to support development, please open an issue to discuss.

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for the full license text.
```

