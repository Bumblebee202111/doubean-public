doubean
=======

An unofficial [Douban][douban] app for browsing [Groups][douban-groups] and exploring Subjects (books/movies/TVs)
\([豆瓣][douban]非官方app，支持[小组][douban-groups]浏览和书影探索\)

**Disclaimer:** This is a continuously evolving project focused on core features using Jetpack Compose. Some features are a work-in-progress (WIP), and you may encounter bugs.


[douban]: https://www.douban.com/
[douban-groups]: https://www.douban.com/group/

## Features

- Subjects (Books/Movies/TVs):
  - Home feed with personal statuses, subject unions, and rank lists
  - View subject interests
  - Search for subjects
  - View subject details, including metadata, intros, and user interactions
  - Browse rank lists
- Groups:
  - Browse my groups and topics
  - Explore group details, topics, and comments
  - Search for groups
  - Enhancements:
    - Pin group tabs locally for quick access
    - Sort topics within each page by create time or default order
    - Customizable topic notifications
- Doulists:
  - View doulists and their items (topics, subjects, reviews)
  - Collect subjects and topics into doulists
  - My Doulists: Collected items + doulists
- General:

  - Ad-free and lightweight (~3MB)
  - Login support & session reuse (advanced, root required)
  - Partial deep linking
  - Direct official API calls
  - Basic Material Design 3 (MD3) theming
  - Basic image viewer
  - User profile screen
  - View statuses feed (legacy)
  - Mobile-optimized pure Web screens (legacy)

## Screenshots

<p> <img src="screenshots/phone_subjects.png" alt="phone_subjects" height="300" />
<img src="screenshots/phone_groups_home.png" alt="phone_groups" height="300" />
<img src="screenshots/phone_group_detail.png" alt="phone_group_detail" height="300" />
<img src="screenshots/phone_topic.png" alt="phone_topic" height="300" />
<img src="screenshots/phone_doulist.png" alt="phone_doulist" height="300" />
<img src="screenshots/tablet_movie.png" alt="tablet_movie" height="450" />
<img src="screenshots/tablet_interests.png" alt="tablet_interests" height="450" /> </p>



## Getting Started

- Download the [Latest release][latest-release]
- Install on Android: 8.1+
- Feel free to [file issues](https://github.com/Bumblebee202111/doubean-public/issues)
- Keep official app: Recommended for full platform functionality
- Troubleshooting: If crashes occur after updates, try clearing app data

[latest-release]: https://github.com/Bumblebee202111/doubean-public/releases/latest

## Important Notices

### Source Code Status

Only selected source code from a private project is shared here to avoid potential issues. More may be synced later.

### Guidelines

- For personal learning & exploration
- Please avoid wide promotion

## Tech Stack

- [Foundation][foundation]: [Android KTX][android-ktx]
- [Architecture][arch]: [Lifecycles][lifecycle], [Navigation][navigation], [Paging][paging], [Room][room], DataStore, [ViewModel][viewmodel], [WorkManager][workmanager]
- [UI][ui]: Jetpack Compose
- Behavior: [Notifications][notifications]
- Third-party libraries
  - [Kotlin Coroutines][kotlin-coroutines], Kotlinx Serialization, Ktor, Coil, libsu, Accompanist Permissions/WebView

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

### Current release \(0.9.9)

- Fix contentPadding of Group Searches

### Next release \(0.9.10)

- i18n: Language selection

<details>
<summary>Future plans (Click to expand)</summary>

#### High Priority

#### Backlog
- Navigation
  - Consider use single NavHost (?)
  - Navigation 3 when beta
  - Allow customizing bottom navigation ([#16](https://github.com/Bumblebee202111/doubean-public/issues/16))
  - Shared element transitions for images
- Groups 
  - Interactions
    - Pin my groups
    - Official-style list item actions
  - Simplify topic caching
  - Search:
    - Topics within a group/tab
    - Global topic search
    - Tabbed search results
  - Notifications: Add 1h interval option
  - Home: Potential pagination for recommended topics
  - Tab
    - Track topic read status
    - Dynamic topic sorting options
  - Group Detail
    - Auto-collapse header for subscribed/pinned groups/tabs
    - Fix description action overflows
  - Topic:
    - Fully load content via official script (no manual parsing)
    - Fix content scroll restoration
  - Default hide officially-flagged "unfriendly" content
  - Implement content blocking
  - Reddit-style list item expand/collapse
  - Investigate API for *all* followed topics like desktop Web app (currently seems unlikely)
    - Use as home/notifications data source
- Subjects
  - Search: Fix filter auto-selecting the wrong tag when the first option is hidden (e.g., "林俊杰")
  - Subject Detail: Ratings histogram
  - Color schemes
  - More
- UserProfile: My subjects, My groups
- Doulists
  - Edit/remove posts
  - Follow Doulists
  - List/post pagination
- Persist last-active tab
- Expand/collapse component: Hide action for short text
- Search suggestions
- Login: Code-based (w/o pw)
- Shortcuts
- Improve login prompts & tooltips
- Use bottom sheets for list item menus
- NetworkManager
- Widgets: Calender today
- QR
- (Rooted users) Optional sync of more existing preferences for API request consistency
- Statuses (maintenance mode)
  - Additional card types
  - Pagination

#### Ongoing Improvements

- Improve list loading states (e.g., retry buttons)
- Ensure basic dark mode, landscape & tablet support
- Sync more source to public repo
- Full deep linking support
- Improve model layering
- i18n: Translations
- Test

</details>

## References

- Official and unofficial sample apps
- [Developer Guides][guides]
- [Material Design][material]
- Various social apps (UI/UX inspiration)

[guides]: https://developer.android.google.cn/guide
[material]:https://material.io/

## Utilities

- [HTTPCanary][http-canary] for analyzing API
- [jadx][jadx]-gui for model understanding
- [Google Chrome][google-chrome] for debugging CSS

[http-canary]:https://github.com/MegatronKing/HttpCanary/

[jadx]:https://github.com/skylot/jadx/releases

[google-chrome]:https://www.google.com/chrome/

## Stars :star: & Donation :coffee:

Your ⭐ **stars** motivate continued development! If you find this project helpful and wish to support its development, you can treat me to a coffee via WeChat Pay: 

<p align="center">  <img src="assets/wechat_donate_qr.png" alt="WeChat Donate QR Code" width="200"/> </p> 

Thank you for your support!

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for the full license text.
