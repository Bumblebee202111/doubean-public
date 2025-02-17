doubean
=======

An unofficial [Douban][douban] app mainly used for browsing the [Groups][douban-groups] section. Subjects module is under construction.
\([豆瓣][douban]非官方app，主要用于[小组][douban-groups]浏览。书影~~音~~模块开发中。\)

Note: This personal project implements essential and user-requested features. It also serves as a playground for experimenting with trending technologies like Jetpack Compose. Occasional crashes, incomplete UI designs, or missing features may occur.

**Known issues:**

[douban]: https://www.douban.com/

[douban-groups]: https://www.douban.com/group/

Introduction
------------

### Screens

#### Statuses Screen

- Following statuses

#### Subjects Screens

##### Home Screen

- Top 250 Movies/Books
- My movie/TV/book statuses

##### Interests Screen

- My subject statuses with interests

##### Search Subjects Screen

- Search subjects

##### Movie/Tv/Book  Screen

- Header
- Intro
- Interests
- More

##### Rank List Screen

- First page of a rank list

#### Groups Screens

##### Home Screen

* My groups
* Favorite groups and tabs
* My topics (or groups of the day for guests)


##### Group Detail Screen

* Group information
* Group tabs

##### Topic Detail Screen

* Topic content
* Topic comments

##### Reshare Statuses Screen

- Reshare statuses

##### Groups Search  Screen

* Search groups
* Groups of the day

##### Notifications Screen 

* Group topic notifications

#### Profile Screen

- Login status

#### Image Screen

- Image view and save

**Login Screen**

- Session login
  - Guide
  - Manual submission


### Screenshots

<p> <img src="screenshots/tablet_movie.png" alt="tablet_movie" height="450" />   
    <img src="screenshots/phone_subjects.png" alt="phone_subjects" height="300" />
<img src="screenshots/phone_search_subjects.png" alt="phone_search_subjects" height="300" />
<img src="screenshots/tablet_interests.png" alt="tablet_interests" height="450" />   
<img src="screenshots/phone_groups_home.png" alt="phone_groups" height="300" />
<img src="screenshots/phone_group_detail.png" alt="phone_group_detail" height="300" />
<img src="screenshots/phone_group_search.png" alt="phone_group_search" height="300" />
<img src="screenshots/phone_post_detail.png" alt="phone_post_detail" height="300" /></p>



### Getting Started

### Using this app

- [Latest release][latest-elease]
- Supported Android versions: 8.1 - 14 (and virtually 15)
- Feel free to file issues
  - If certain data doesn’t display, it’s likely due to a JSON parsing error. Including details like the content title, related URLs (obtained from the Douban app), or raw JSON responses (if possible) in your issue can help resolve the issue faster.
- It's recommended to keep the Douban app coexist
  - Support the official product
  - Some content is missing in doubean
  - Especially for [rooted users](#reuse-login-session)
- Occasionally, you may need to manually clear app data after installation if I forget to handle ROOM database schema changes properly.

[latest-elease]: https://github.com/Bumblebee202111/doubean-public/releases/latest

### Why isn’t it open-source?

To avoid potential issues, pushing the source code (not the release APKs) to the public repository has been suspended. However, if you’re interested in non-sensitive parts of the code, I may consider sharing them upon request.

### Your do's and don'ts

- **Do:** Use this app only for personal learning purposes.
- **Don’t:** Share it on other websites or apps without permission.

### Features

* Provides a simple type-based Subjects (书影~~音~~) experience with support for user actions (WIP 🚧)
* Groups
  * Favorite tabs/groups locally
  * Customizable topic notifications
* Ad-free and lightweight: ~4MB
* Rooted users can reuse the **existing** login session from Douban app
  * Note: Phone login is not supported

* Partial support for URL deep links
* Partial offline caching support for Groups
* Very basic Statuses tab (no longer updated)
* Partial support for viewing content in a mobile-optimized Douban WebView (no longer updated).
* Basic use of MD 2/3 (UI may look inconsistent during migration)

Libraries Used
--------------

* [Foundation][foundation]
  * [AppCompat][appcompat]
  * [Android KTX][android-ktx]
* [Architecture][arch]
  * [Lifecycles][lifecycle]
  * [Navigation][navigation]
  * [Paging][paging]
  * [Room][room]
  * DataStore
  * [ViewModel][viewmodel]
  * [WorkManager][workmanager]
* [UI][ui]
  * [Animations & Transitions][animation]
  * Jetpack Compose
  * [Fragment][fragment]
  * [Layout][layout]
* Behavior
  * [Notifications][notifications]
* Third party and miscellaneous libraries
  * [Kotlin Coroutines][kotlin-coroutines]
  * Kotlinx Serialization
  * Ktor
  * Coil
  * libsu

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

[animation]: https://developer.android.com/training/animation/

[fragment]: https://developer.android.com/guide/components/fragments

[layout]: https://developer.android.com/guide/topics/ui/declaring-layout

[notifications]: https://developer.android.com/develop/ui/views/notifications

[kotlin-coroutines]: https://kotlinlang.org/docs/reference/coroutines-overview.html

### Plans

Incoming features, bug fixes, libraries to use and environment changes \(roughly in chronological order\). Primarily for myself.

#### To-dos for current release \(0.7.13\)

#### To-dos for next release \(0.7.14)


#### Future plans

* Allow non rooted users (guests) to browse personal content if a user ID is provided

* Subjects

  * Pagination of RankListScreen

  * (Single-)status interests screen & its pagination
  * Interest buttons
    * Consider tonal style
  * Tweak reviews sheet for better arrangement of review counts
  * More details & sub-features

* Remember last bottom/Subjects tabs

* Do not use ugly Thumb Up icon of Material Icons

* Groups
  * Dynamic topic sorts-by

  - Re-evaluate ROOM caching, as it added unnecessary complexity
  - Optimize colors for group-themed screens

  * Home
    * Maybe support pagination of recommended topics
  * Subscribe/favorite/save
    * Save topics
    * Pin my groups
    * Add corresponding item actions
  * Group tab: Track read status
  * Search
    * Search topics within a group/tab
    * Search topics across all groups (global)
  * Use the new nullable "edit_time" property of network topics
  * Revert naming conventions to match Douban's standards
  * Group Detail: Collapse on entrance for the subscribed/favorited group/tab
  * Expand/collapse component: Hide action for short text
  * Groups: Group/tab/topic shortcuts
  * Topic content WebView problems
    * Flinging up to topic content is not smooth https://issuetracker.google.com/issues/172029355
    * Sometimes topic content fails to be loaded on some low-end or large-screened devices, or of special layout (not sure which sets constraint)
    * LazyLayoutPinnableItem?
    * ...
  * Hide officially-marked unfriendly content by default
  * Support blocking unfriendly content
  * Lists: Reddit-like item expand/collapse
  * Find API for all followed topics (currently unlikely)

* Properly show login prompts & normal tips/info

* Status bar color

* Error handling

  * Process error body
  * Show error messages

* Load state visualization (removed during migrations for simplicity)
  * Paging 3 refresh, loading status ...

* Restore basic support for dark mode & landscape experience

* Migrate `WebView`, `RatingBar`, and `PreferenceFragmentCompat` to Jetpack Compose and MD3.

* Shared element

* Improve model layering

* Languages
  * Translations
  * Pick language

* NetworkManager

* Widgets

* Reuse more existing Douban preferences to make requests more consistent between the two apps

* Consider use single NavHost

* Independent login (currently unlikely)

* Statuses (maintenance state)
  * More card types
  * Pagination

* Test

### Non-Todos

* Heavy use of Material Design

### References

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

### Utilities

* [HTTPCanary][http-canary] for analyzing Douban API requests
* [jadx][jadx]-gui for understanding models
* [Google Chrome][google-chrome] for debugging CSS

[http-canary]:https://github.com/MegatronKing/HttpCanary/

[jadx]:https://github.com/skylot/jadx/releases

[google-chrome]:https://www.google.com/chrome/

### Stars :star: & Donation :coffee:

I know this app isn’t perfect, but the growing number of stars keeps me motivated to keep working on it. Thank you for your support!

If you find the app really useful and would like to buy me a coffee (though I’m not actively looking for donations), you can contact me by creating an issue.

