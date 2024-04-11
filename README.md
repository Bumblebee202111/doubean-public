doubean
=======

An unofficial [Douban][douban] app mainly used for browsing the [Groups][douban-groups] section.
\([豆瓣][douban]非官方app，主要用于[小组][douban-groups]浏览。\)

The development of this app will soon been restarted but still not progressed on a frequent basis, as is requested by the friend who filed the first issue! While we will only develop for the only user, this project is also going to be used as the playground for new libraries such as Jetpack Compose, partially serving the development of MinusOne Music and helping me understand declarative programming.

It has been inactive for 8 months, as:

* Reinforced by the org/mods, Douban users are making hilarious remarks, which are frustrating to read
* There are few alternatives for me to refer to
* Group theming is currently a mess

Note I am an inexperienced Android beginner and the only developer of it. Occasional crashes may happen, UI design may be incomplete and features you need may be
missing. Pretty much of my work is copy and paste from the official documentation and samples of Google Android. I am trying my best only to ensure that the app will function normally on my Xiaomi devices with the Android version varying from 8.1 to 12.

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

This project uses the Gradle build system. To build this project, use the `gradlew build` command or use "Import Project" in Android Studio.

#### Douban access

To not get myself into trouble, I have added detailed implementation of data access to .gitignore list. However, the latest release is always available.

### Who is it for?

* Me and other developers who are learning Android, Kotlin and version control
* Me and other users who want easier access to Douban groups module

### Features

It not only implements the very basic features of Douban app, but also has its own features. Some of which react to the fact that Douban community is somewhat unfriendly and closed to non-members;
examples are power abuse of some group mods, "apply ~~to follow or~~ to talk" and etc.

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
* Support creating categories consisting of group/tab/search posts by multiple user-defined
  conditions \(TODO\)

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
  * [ViewModel][viewmodel]
  * [WorkManager][workmanager]
* [UI][ui]
  * [Animations & Transitions][animation]
  * [Fragment][fragment]
  * [Layout][layout]
* Behavior
  * [Notifications][notifications]
* Third party and miscellaneous libraries
  * [Retrofit][retrofit]
  * [Apache Commons][apache-commons]
  * [Glide][glide]
  * [Kotlin Coroutines][kotlin-coroutines]

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

* Password login
* Groups home for login

#### To-dos for next release \(0.5.1\)

#### Future plans

* Login if possible \(else exclude modules other than Groups from the to-do list\)
* Migrate GithubBrowserSample's user-implemented way of paging to Paging3
* Use the new nullable "edit_time" property of network posts
* Split Douban data and local user data instead of putting them in one model class
* Find a better solution for group theming
* Refactor `Resource` to `sealed` `Result`, parse it in ViewModels \(like iosched,
  architecture-samples)\
* Learn about RecycledViewPool and use it if necessary
* \[Books\] Add T250
* \[Movies\] Add T250
* Hide officially-marked unfriendly content by default
* Optimize rendering of pictures
* Hilt
* Allow expanding group description w/ SpannableString
* \[SettingsFragment\] Add WebView enabled by default preference, nav start destination etc with
  DataStore
* \[SearchFragment\]GroupSearchFragment -> SearchFragment / Search group/tab posts (in-group) /
  Search posts of all groups (global)
* Add very basic testing if possible
* \[PostDetailFragment\]Save WebView images
* \[GroupDetailFragment\]WebView for group
* \[Build\] Migrate to KSP
* \[WebView\]Complete CSS of content WebView: find official CSS source or write on my own
* \[GroupDetailFragment\]\[PostDetailFragment\]Track/mark/revert/todo read
* \[GroupsHomeFragment\]Unfollow/reorder items
* Follows/saves
  * Allow following/saving every type of item: groups/tabs/searches/categories
  * Post title text filters
  * \[GroupsHomeFragment\]Show posts of followed collections / comments of saved comments with
    group/tab header
  * \[Lists\]Add corresponding item action
  * Custom feeds like Reddit
    * Replace content of the recommended groups area with recommended posts \(of followed groups
      and
      tabs\)
* \[GroupDetailFragment\]Group/tab/post shortcuts
* Rate limit
* \[Lists\]Reddit-like item expand/collapse
* \[GroupDetailFragment\]For post items, optimize tag display, e.g., assign color to each tag mapped
  from name
* 🛠\[GroupPostFragment\]Dealing with post URL click - new page should not show in the old WebView
* Support blocking unfriendly content
* \[PostDetailFragment\]Show saves and reposts
* \[GroupDetailFragment\]Collapse on entrance for the followed group/tab
* \[Sortby\]Migrate Spinner to `singleSelection` `ChipGroup`
* Allow another option of using Twitter-like date formatting
* Widgets
* More features of books & movies
* \[GroupDetailFragment\] The problem that if the initial position is manually set, the last
  position won't be restored when the pager is being recreated is solved, but not in a way that is
  clean enough
  * Should I use Fragment.isResumed?

* 🛠 Views on top of WebView become invisible after scroll on my MIUI 10 Android 8.1 device \(causes
  unknown, may never be fixed\)
* \[Lists\]Post order: custom rules based on multiple factors
* User data backup

#### More general plans

* Use new features of the updated dependencies
  * CreationExtras?
* Optimize landscape experience
* Decouple Douban Interface and expose as much code to VC as possible \(Make anything but keys
  open-source\)

### Non-Todos

* Heavy use of Material Design

### References

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

License
------------

    Copyright 2023 Bumblebee202111. All rights reserved.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Besides adhering to the license, I wish that you will use it only for
learning/personal/non-commercial purposes, and won't propagate it on other websites/apps, out of
concern for potential copyright violations.

Anecdotes
------------

### Efforts to Solve the dynamic ViewPager2 problem

The problem is that when an ViewPager2 is asynchronously loaded via its FragmentStateAdapter from a LiveData containing info of its tabs \(pages\), either the pager position or the page position will never be saved, unlike the behavior of a RecycleView with its customized ListAdapter. LiveData +
ViewPager2 seemed to be a very unpopular combination, and there is little info about it online.
Hence, the issue had obsessed me for months and discouraged me to continue the development. On April
10th in 2023, I decided to have my last fight against it. What I did:

1. Download Douban APK version 6.40.0 \(less obscuration, faster decompilation\)
2. Decompile it via JADX-GUI and export it to gradle project
3. Open the project in Android Studio and research on what the Douban implementation is. \(Brief
   conclusion: GroupDetailActivity + FragmentStatePagerAdapter + Intent/Bundle\)

I repeated these steps on version 7 & 8, only to find that the ugly and lengthy code is still almost unchanged. It really surprised me. In early July, after weeks of architecture optimization, I solved it by attaching adapter only when data is ready, and used an Event wrapper to set the default position \(which would soon be replaced by a Coroutine method\) .