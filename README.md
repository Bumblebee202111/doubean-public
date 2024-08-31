doubean
=======

An unofficial [Douban][douban] app mainly used for browsing the [Groups][douban-groups] section.
\([豆瓣][douban]非官方app，主要用于[小组][douban-groups]浏览。\)

Note: I am an inexperienced Android beginner and the only developer of it. Occasional crashes may happen, UI design may be incomplete and features you need may be missing. Only features demanded by me and other users are possible to be added. This project is also going to be used as my playground for trending technologies such as Jetpack Compose. This brings tons of migration tasks ([remaining ones](#migrations)), and also means that old and new libraries are currently mixed.

*The development of this app is slowed down again so that I can spend more time on the exploration of the backend and frontend areas.*

[douban]: https://www.douban.com/

[douban-groups]: https://www.douban.com/group/

Introduction
------------

### Screens

#### Home Screen

- Following

#### Image Screen

- Image view and save

#### Groups Screens

##### Home Screen

* My groups
* Favorite groups and tabs
* My topics (or groups of the day, for guests)


##### Group Detail

* Group information
* Group tabs

##### Topic Detail

* Topic content
* Topic comments

##### Groups Search

* Search groups
* Groups of the day

#### Profile Screen

- Login status

**Login Screen**

- Login guide
- Manual session login

#### Notifications Screen 

* Group topic notifications (the feature is broken now)

### Screenshots

<p><img src="screenshots/phone_groups.png" alt="phone_groups" height="300" />
<img src="screenshots/phone_group_detail.png" alt="phone_group_detail" height="300" />
<img src="screenshots/phone_group_search.png" alt="phone_group_search" height="300" />
<img src="screenshots/phone_post_detail.png" alt="phone_post_detail" height="300" /></p>

### Getting Started

### Using this app

- Supported Android versions: 8.1 - 14
- Sometimes you need to manually clear app data when I forget to handle ROOM database schema change
- Feel free to file issues 

### Open-source?

To not get this project/myself into trouble, the update of the source code in the public repo has been suspended. However, the latest release is always available.

### Who is it for?

* Me and other developers who are learning Android, Kotlin and version control
* Me and other users who desire easier access to Douban, especially the Groups module of it.

### Your do's and don'ts

- Please use it only for personal learning purposes. 
- Please don't share it on other websites/apps without permission.

### Features

It not only implements some features (mainly of Groups) of Douban app, but also has its own features. 

* Partial offline caching support
* Ad-free, lite \(~4MB\)
* Partially support URL deep links
* Rooted users can reuse login session of Douban app
* Partial support for viewing content in Douban WebView optimized for mobile reading in case of need (no longer updated)
* Basic use of MD2/3 (UI may not look good during migration)
* Recommended topic notifications (maybe currently broken)

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
  * [Data Binding][data-binding]
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

[data-binding]: https://developer.android.com/topic/libraries/data-binding/

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

Incoming features, bug fixes, libraries to use and environment changes \(roughly in chronological order\)

#### To-dos for current release \(0.6.3\)

"Sync is expected to be done ..."

"Too hard to implement"

#### To-dos for next release \(0.6.4)

- Reuse item logic of search result groups / groups of the day

#### Future plans

* Gradually revert naming conventions to those of Douban
* Gracefully remove Fragments: First reduce usages of Fragment methods
* Gradual migrations: <span id="migrations"></span>
  * From MD2 / custom design to MD3
  * From View and Data Binding to Jetpack Compose
    * Exceptions: WebView/RatingBar/PreferenceFragmentCompat/CollapsingToolbarLayout
* Groups - Home
  * Support pagination?
  * Add favorite tabs for topic list area
* Home - Following
  * More card types
  * Pagination
* Edge-to-Edge
* Show error messages
* Bring back load state visualization which was removed for simplicity during various types of migrations
  * Paging 3 refresh, loading status ...
* Subscribe/favorite/save
  * Save topics
  * Pin my groups
  * Add corresponding item actions
* Group Detail
  * Compose collapsing toolbar when more convenient
  * Collapse on entrance for the subscribed/favorited group/tab
* Allow expanding group description w/ SpannableString
* Bring back basic support for dark mode & landscape experience
* Topics sort by 
  * "Last updated" -> "New (last updated)"
  * Add "New Top"
* Shared element
* `Lazylist` problems
  * Restoration of scroll position is problematic
* Topic content WebView problems
  * Flinging up to topic content is not smooth https://issuetracker.google.com/issues/172029355
  * Sometimes topic content fails to be loaded on some low-end or large-screened devices, or of special layout (not sure which sets constraint)
  * LazyLayoutPinnableItem?
  * ...
* Group tab: Track read
* Topics feed (notifications)
  * Find why it is broken and fix it
  * Clarify:
    * Improper use may disturb you and drain phone battery (untested)
    * Better keep app in background
    * Mechanism: Actively query group topics (without tab_id parameter, to increase efficiency) by each group
  * “每次动态更新请求的帖子总数限制” -> ?
  * Create reusable class for all notification settings
* Use the new nullable "edit_time" property of network topics
* Search
  * Search group/tab topics (in-group) 
  * Search topics of all groups (global)
* Better model layering
* Languages
  * Translations
  * Option
  * Distinguish`en-DB`/`en-US`
* More subscribe/favorite/save
  * Save comments
  * Subscribe topics
  * Sync custom lists by maintaining a fake private note which holds the data
  * Custom feeds like Reddit
* Groups: Group/tab/topic shortcuts
* Hide officially-marked unfriendly content by default
* Support blocking unfriendly content
* NetworkManager
* Widgets
* \[Books\] Add T250
* [Movies\] Add T250
* More features of books & movies
* Lists: Reddit-like item expand/collapse
* Group Detail: For topic items, optimize tag display, e.g., assign color to each tag mapped from ID
* Reuse more existing Douban preferences
* Independent login (seems impossible)
* Find API for all followed topics (seems impossible)
* Group Detail: WebView for group
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
* Interactive communities: [StackOverflow][stack-overflow], [Google][google], [GitHub][github]
  , [cnblogs][cnblogs], [Medium][medium], [CSDN][CSDN], IssueTracker, etc.
  * Especially went through articles/posts on how to gain Douban access
* Various social apps as references for UI design

[sunflower]: https://github.com/android/sunflower

[guides]: https://developer.android.google.cn/guide

[github-browser-sample]: https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample

[todo-mvvm-live]: https://github.com/android/architecture-samples/tree/todo-mvvm-live

[material]:https://material.io/

[stack-overflow]: https://stackoverflow.com/

[google]: https://www.google.com/

[cnblogs]: https://www.cnblogs.com/

[github]:https://github.com/

[medium]:https://medium.com/

[csdn]: https://blog.csdn.net/

### Utilities

* [HTTPCanary][http-canary] for douban access
* [jadx][jadx]-gui for understanding models
* [Google Chrome][google-chrome] for CSS debugging

[http-canary]:https://github.com/MegatronKing/HttpCanary/

[jadx]:https://github.com/skylot/jadx/releases

[google-chrome]:https://www.google.com/chrome/

### Stars :star: & Donation :coffee:

I'm totally aware that this app is mediocre. However, the increasing number of stars keep me motivated! Thank you!

If you find it really useful and would like to buy me a coffee, which I am not ready for, please contact me by creating an issue.

