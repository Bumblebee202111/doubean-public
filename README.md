doubean
=======

An unofficial [Douban][douban] app mainly used for browsing the [Groups][douban-groups] section.
\([豆瓣][douban]非官方app，主要用于[小组][douban-groups]浏览。\)

The development of this app is restarted but still not progressed on a frequent basis, to meet the requirements the friend who filed the first issue! While we will only develop for them, this project is also going to be used as the playground for fashionable libraries/coding styles such as Jetpack Compose, partially serving the development of MinusOne Music and helping me understand declarative programming. This brings [tons of migration tasks](#migrations), and also means that old libraries and new libraries are currently mixed.

It has been inactive for 8 months, as:
* Reinforced by the org/mods, hilarious remarks can be found everywhere
* There are few other alternatives for me to refer to
* Group theming is currently a mess

Note I am an inexperienced Android beginner and the only developer of it. Occasional crashes may happen, UI design may be incomplete and features you need may be
missing. Pretty much of my work is copy and paste from the official documentation and samples of Google Android. I am trying my best only to ensure that the app will function normally on my Xiaomi devices with the Android version varying from 8.1 to 12 and the user's device.

**Very important changes started from 0.5.0:**

* Repo change: `doubean` -> `doubean-public`
  - New code will only be pushed into the private `doubean`
* Package name change: now `com.github.bumblebee202111.doubean`

[douban]: https://www.douban.com/

[douban-groups]: https://www.douban.com/group/

Introduction
------------

### Functionality

The app is composed of 3 main screens, two of which are blank and left for future development.

#### GroupFragment

##### Groups Home

* Followed groups and tabs

##### Group Detail

* Group information
* Group tabs

##### Post Detail

* Post content
* Post comments

##### ProfileFragment

- Login status

##### Groups Search

* Search groups

#### HomeFragment

Blank

#### NotificationFragment

* Group post notifications

### Screenshots

<p><img src="screenshots/phone_groups.png" alt="phone_groups" height="300" />
<img src="screenshots/phone_group_detail.png" alt="phone_group_detail" height="300" />
<img src="screenshots/phone_group_search.png" alt="phone_group_search" height="300" />
<img src="screenshots/phone_post_detail.png" alt="phone_post_detail" height="300" /></p>

### Getting Started

#### Open-source?

To not get this project/myself into trouble, the update of the source code of the public repo will be suspended. However, the latest release is always available.

### Who is it for?

* Me and other developers who are learning Android, Kotlin and version control
* Me and other users who want easier access to Douban groups module

### Your do's and don'ts

- Please use it only for personal learning purposes. 
- Please don't share it on other websites/apps without permission.

### Features

It not only implements the very basic features of Douban app, but also has its own features. Some of which react to the fact that Douban community is somewhat unfriendly and closed to non-members; examples are power abuse of some group mods, "apply ~~to follow or~~ to talk" and etc.

\* In more recent (after mid-2022) versions of the Douban app, the feature of following unjoined groups is finally added by them.

* Inherent advantages leading to good performance and simple coding

| Design Aspect     | Choice\(s\)                                     |
|-------------------|-------------------------------------------------|
| Language          | Kotlin                                          |
| Libraries         | Jetpack and authoritative third party libraries |
| Architecture      | MVVM                                            |
| Design philosophy | Android Jetpack                                 |

* Support for loading cache as alternative even when connection is off
* Recommended post notifications
* Ad-free, lite \(~5MB\)
* Support URL deep links
* Basic support for MD \(dark theme included\) and tablets/landscape
* Bilingual UI strings support
* Support for viewing content in Douban WebView optimized for mobile reading \(in progress\)
* Paging & swipe refresh support
* Rooted users can use login session of Douban app
* Support creating categories consisting of group/tab/search posts by multiple user-defined conditions \(TODO\)

Libraries Used
--------------

* [Foundation][foundation]
  * [AppCompat][appcompat]
  * [Android KTX][android-ktx]
  * [Test][test] \(TODO\)
* [Architecture][arch]
  * [Data Binding][data-binding]
  * [Lifecycles][lifecycle]
  * [LiveData][livedata]
  * [Navigation][navigation]
  * [Paging][paging]
  * [Room][room]
  * DataStore
  * [ViewModel][viewmodel]
  * [WorkManager][workmanager]
* [UI][ui]
  * [Animations & Transitions][animation]
  * [Fragment][fragment]
  * [Layout][layout]
  * Jetpack Compose
* Behavior
  * [Notifications][notifications]
* Third party and miscellaneous libraries
  * [Retrofit][retrofit]
  * [Apache Commons][apache-commons]
  * [Glide][glide]
  * [Kotlin Coroutines][kotlin-coroutines]
  * Flow
  * Ktor
  * Kotlinx Serialization
  * libsu

[foundation]: https://developer.android.com/jetpack/components

[appcompat]: https://developer.android.com/topic/libraries/support-library/packages#v7-appcompat

[android-ktx]: https://developer.android.com/kotlin/ktx

[test]: https://developer.android.com/training/testing/

[arch]: https://developer.android.com/jetpack/arch/

[data-binding]: https://developer.android.com/topic/libraries/data-binding/

[lifecycle]: https://developer.android.com/topic/libraries/architecture/lifecycle

[livedata]: https://developer.android.com/topic/libraries/architecture/livedata

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

[retrofit]: https://square.github.io/retrofit/

[apache-commons]: https://commons.apache.org/

[glide]: https://bumptech.github.io/glide/

[kotlin-coroutines]: https://kotlinlang.org/docs/reference/coroutines-overview.html

### Plans

Incoming features, bug fixes, libraries to use and environment changes \(roughly in chronological
order\)

#### To-dos for current release \(0.5.0\)

#### To-dos for next release \(0.5.1\)

#### Future plans

* User requirements (= high priority)
  * Home - Following
  * Rename local follow to favorite
  * Replace content of the recommended groups area with recommended posts
    * First tab/page: Post recommendation (of followed groups) API
    * Others: posts of favorite tabs from group tab posts API

  * Post detail
    * Polls
    * Comment image

* Gradual migrations: <span id="migrations"></span>
  * From MD2 / custom design to MD3
  * From manual DI to Hilt
  * From Gson to Kotlinx.Serialization
  * From custom paging to Paging 3
  * From GithubBrowserSample's user-implemented way of paging to Paging3
  * From LiveData to StateFlow
  * From View and data binding to Jetpack Compose
  * From Retrofit to Ktor
  * From Glide to Coil Compose
  * From Apache Commons to ??? 
  * From NavHostFragment to ComposableNavHostFragment
  * From Navigation Fragment to Navigation Compose (start when next stable version is available)

* “每次动态更新请求的帖子总数限制” -> “每次从n条帖子中筛选新动态”
* Use the new nullable "edit_time" property of network posts
* Split Douban data and local user data instead of putting them in one model class
* Refactor uses of network only `Resource` to Kotlin `Result`
* Set dispatchers / handle errors in repositories
* \[Books\] Add T250
* \[Movies\] Add T250
* Hide officially-marked unfriendly content by default
* Allow expanding group description w/ SpannableString
* \[SettingsFragment\] Add WebView enabled by default preference, nav start destination etc with DataStore
* \[SearchFragment\] GroupSearchFragment -> SearchFragment / Search group/tab posts (in-group) / Search posts of all groups (global)
* Add very basic testing if possible
* Real login (which seems impossible)
* \[PostDetailFragment\] Save WebView images
* \[GroupDetailFragment\] WebView for group
* \[WebView\]Complete CSS of content WebView: find official CSS source or write on my own
* \[GroupDetailFragment\]\[PostDetailFragment\]Track/mark/revert/todo read
* \[GroupsHomeFragment\]Unfollow/reorder items
* Follows/saves
  * Allow following/saving every type of item: groups/tabs/searches/categories
  * Post title text filters
  * \[GroupsHomeFragment\] Show posts of followed collections / comments of saved comments with group/tab header
  * \[Lists\]Add corresponding item action
  * Custom feeds like Reddit
* \[GroupDetailFragment\] Group/tab/post shortcuts
* Rate limit
* \[Lists\]Reddit-like item expand/collapse
* \[GroupDetailFragment\]For post items, optimize tag display, e.g., assign color to each tag mapped from name
* 🛠\[GroupPostFragment\]Dealing with post URL click - new page should not show in the old WebView
* Support blocking unfriendly content
* \[PostDetailFragment\] Show saves and reposts
* \[GroupDetailFragment\] Collapse on entrance for the followed group/tab
* \[Sortby\]Migrate Spinner to `singleSelection` `ChipGroup`
* Allow another option of using Twitter-like date formatting
* Widgets
* More features of books & movies
* \[GroupDetailFragment\] Rewrite
* 🛠 Views on top of WebView become invisible after scroll on my MIUI 10 Android 8.1 device \(causes
  unknown, may never be fixed\)
* \[Lists\]Post order: custom rules based on multiple factors
* User data backup
* Optimize landscape experience

### Non-Todos

* Heavy use of Material Design

### References

* New Compose samples
* [Android Sunflower][sunflower]
* [Developer Guides][guides]
* [Github Browser Sample with Android Architecture Components][github-browser-sample]
* [Android Architecture Blueprints v1 (todo-mvvm-live)][todo-mvvm-live]
* [Material Design][material]
* Interactive communities: [StackOverflow][stack-overflow], [Google][google], [GitHub][github]
  , [cnblogs][cnblogs], [Medium][medium], [CSDN][CSDN], etc.
  * Especially went through articles/posts on how to gain Douban access
* [Twitter][twitter], [Reddit][reddit], [Play Store][google-play], [CoolApk][coolapk]
  , [Google News][google-news], [YouTube][youtube], [bilibili][bilibili] and other apps as
  references for UI design

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

[twitter]:https://play.google.com/store/apps/details?id=com.twitter.android

[reddit]:https://play.google.com/store/apps/details?id=com.reddit.frontpage

[google-play]:https://play.google.com/store

[coolapk]:https://www.coolapk.com/

[google-news]:https://play.google.com/store/apps/details?id=com.google.android.apps.magazines

[youtube]:https://play.google.com/store/apps/details?id=com.google.android.youtube

[bilibili]: https://play.google.com/store/apps/details?id=com.bilibili.app.in

### Utilities

* [HTTPCanary][http-canary] for douban access
* [jadx][jadx]-gui for understanding models
* [Google Chrome][google-chrome] for CSS debugging

[http-canary]:https://github.com/MegatronKing/HttpCanary/

[jadx]:https://github.com/skylot/jadx/releases

[google-chrome]:https://www.google.com/chrome/

