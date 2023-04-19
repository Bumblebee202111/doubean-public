doubean
=======

An unofficial [Douban][douban] app mainly used for browsing the [Groups][douban-groups] section.
\([豆瓣][douban]非官方app，主要用于[小组][douban-groups]浏览。\)

From now on, this repo will be updated slightly more frequently as I will probably use it as a '
ticket' to my first Android job. Note I am an inexperienced Android beginner and the only developer
of it. Occasional crashes may happen, UI design may be incomplete and features you need may be
missing. Pretty much of my work is copy and paste from the official documentation and samples of
Google Android. I am trying my best only to ensure that the app will function normally on my Xiaomi
devices with Android version varying from 8.1 to 12.

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

Blank

### Screenshots

<p><img src="screenshots/phone_groups.png" alt="phone_groups" height="300" />
<img src="screenshots/phone_group_detail.png" alt="phone_group_detail" height="300" />
<img src="screenshots/phone_group_search.png" alt="phone_group_search" height="300" />
<img src="screenshots/phone_post_detail.png" alt="phone_post_detail" height="300" /></p>

### Getting Started

This project uses the Gradle build system. To build this project, use the
`gradlew build` command or use "Import Project" in Android Studio.

#### Douban access

To not get myself into trouble, I have added detailed implementation of data access to .gitignore
list. However, the latest release is always available.

### Who is it for?

* Me and other developers who are learning Android, Java and version control
* Me and other users who want easier access to Douban groups

### Non-Goals

* Login-based features
* Jetpack Compose
* Dagger2/Hilt
* RxAndroid
* Heavy use of Material Design

### Features

It not only implements the very basic features of Douban app, but also has its own features. Some of
which react to the fact that Douban community is somewhat unfriendly and closed to non-members;
examples are power abuse of some group mods, "apply ~~to follow or~~ to talk" and etc.

\* In more recent (after mid-2022) versions of the Douban app, the feature of following unjoined
groups is finally added by them.

* Inherent advantages leading to good performance and simple coding

  | Design Aspect | Choice\(s\) |
                        | --- | --- |
  | Language | Java |
  | Libraries | Jetpack and authoritative third party libraries |
  | Architecture | MVVM |
  | Design philosophy | Android Jetpack |

* Support for viewing content in Douban WebView optimized for mobile reading
* Support for loading cache as alternative when connection is off
* Ad-free, lite \(~4MB\)
* Support URL deep links
* Basic support for MD \(dark theme included\) and tablets/landscape
* Bilingual UI strings support
* Paging & swipe refresh support
* Support creating categories consisting of group/tab/search posts by multiple user-defined
  conditions \(TODO\)

Libraries Used
--------------

* [Foundation][foundation]
  * [AppCompat][appcompat]
  * [Test][test] \(TODO\)
* [Architecture][arch]
  * [Data Binding][data-binding]
  * [Lifecycles][lifecycle]
  * [LiveData][livedata]
  * [Navigation][navigation]
  * [Room][room]
  * [ViewModel][viewmodel]
* [UI][ui]
  * [Animations & Transitions][animation]
  * [Fragment][fragment]
  * [Layout][layout]
* Third party and miscellaneous libraries
  * [Retrofit][retrofit]
  * [Apache Commons][apache-commons]
  * [Glide][glide]

[foundation]: https://developer.android.com/jetpack/components

[appcompat]: https://developer.android.com/topic/libraries/support-library/packages#v7-appcompat

[test]: https://developer.android.com/training/testing/

[arch]: https://developer.android.com/jetpack/arch/

[data-binding]: https://developer.android.com/topic/libraries/data-binding/

[lifecycle]: https://developer.android.com/topic/libraries/architecture/lifecycle

[livedata]: https://developer.android.com/topic/libraries/architecture/livedata

[navigation]: https://developer.android.com/topic/libraries/architecture/navigation/

[room]: https://developer.android.com/topic/libraries/architecture/room

[viewmodel]: https://developer.android.com/topic/libraries/architecture/viewmodel

[ui]: https://developer.android.com/guide/topics/ui

[animation]: https://developer.android.com/training/animation/

[fragment]: https://developer.android.com/guide/components/fragments

[layout]: https://developer.android.com/guide/topics/ui/declaring-layout

[retrofit]: https://square.github.io/retrofit/

[apache-commons]: https://commons.apache.org/

[glide]: https://bumptech.github.io/glide/

### Plans

Incoming features, libraries and bug fixes \(roughly in chronological order\)

#### To-dos for next release

* \[GroupsHomeFragment\]Add "groups of the day" list & remove prepopulated followed items \(and
  extra repository logic\)
* \[SettingsFragment] Add, which may contain author info, WebView enabled by default toggle, etc
* 🛠\[GroupTabFragment\] A more click should pop up a menu instead of directly starting a share
  action
* 🛠\[Need help\]\[GroupDetailFragment\]The last position won't be restored when the pager is being
  recreated
  * When using ViewPager2, the restore problem arises if the list of page IDs are asynchronously
    submitted by LiveData
  * Try to ask ChatGPT for help
  * It is reallyyyyyyyyy annoying \(SEE the anecdote\)

#### Future plans

* \[Lists\]Migrate GithubBrowserSample's way of paging to Paging3 + Guava/Coroutine + LiveData/Flow
* SnackbarUtils from todo app
* Rate limit
* \[SearchFragment\]GroupSearchFragment -> SearchFragment / Search group/tab posts (in-group) /
  Search posts of all groups (global)
* \[PostDetailFragment\]Save WebView images
* \[GroupDetailFragment\]\[PostDetailFragment\]Track/mark/revert read
* \[GroupsHomeFragment\]Unfollow/reorder items
* \[GroupDetailFragment\]WebView for group
* Allow following/saving every type of item
  * \[GroupsHomeFragment\]Show followed groups/tabs/searches / saved posts / saved comments with
    group info
  * \[Lists\]Add item action
* \[GroupDetailFragment\]Allow custom groups/tabs/posts categories/groups with filters available
* \[WebView\]Complete CSS of content WebView: find official CSS source or write on my own
* \[GroupDetailFragment\]Group/tab/post shortcuts
* \[Lists\]Reddit-like item expand/collapse
* \[GroupDetailFragment\]For post items, optimize tag display, e.g., assign color to each tag mapped
  from name
* 🛠\[GroupPostFragment\]Dealing with post URL click - new page should not show in the old WebView
* Support blocking unfriendly content
* \[GroupsHomeFragment\]Recommendations using SharedPref
* Use Twitter-like date formatting
* Widgets*
* \[PostDetailFragment\]Show saves and reposts
* \[Sortby\]Migrate Spinner to `singleSelection` `ChipGroup`
* Notifications and push services \(delayed in consideration of its difficulty and unimportance\)
* \[GroupDetailFragment\]Collapse on entrance for the followed group/tab
* Other modules, e.g., books, movies
* 🛠 Views on top of WebView become invisible after scroll on my MIUI 10 Android 8.1 device \(causes
  unknown, may never be fixed\)
* \[Data layer\] Migrate to Kotlin Flow
* \[Lists\]Post order: custom rules based on multiple factors
* Uploads copies of responses to some place and reuses it among users to prevent frequent Douban
  visits.

#### More general plans

* Migration to Kotlin
  * New class files should be written in Kotlin
  * Use Kotlin/Flow first in repository/data layer and paging, while the UI layer should keep using
    LiveData even with the Kotlin migration
* Use MD3
* Use new features of the updated dependencies
  * CreationExtras?
* Optimize landscape experience
* Add views for fields currently unused
* Decouple DoubanInterface and expose as much code to VC as possible \(Make anything but keys
  open-source\)

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
Unfortunately, I know little about licensing. However, I wish that you use it for learning/personal
purposes only, and won't propagate it on other websites/apps, out of concern for potential copyright
violations.

Anecdotes
------------

### Efforts to Solve the dynamic ViewPager2 problem

The problem is that when an ViewPager2 is asynchronously loaded via its FragmentStateAdapter from a
LiveData containing info of its tabs \(pages\), either the pager position or the page position will
never be saved, unlike the behavior of a RecycleView with its customized ListAdapter. LiveData +
ViewPager2 seemed to be a very unpopular combination, and there is little info about it online.
Hence, the issue had obsessed me for months and discouraged me to continue the development. On April
10th in 2023, I decided to have my last fight against it. What I did:

1. Download Douban APK version 6.40.0 \(less obscuration, faster decompilation\)
2. Decompile it via JADX-GUI and export it to gradle project
3. Open the project in Android Studio and research on what the Douban implementation is. \(Brief
   conclusion: GroupDetailActivity + FragmentStatePagerAdapter + Intent/Bundle\)

I repeated these steps on version 7 & 8, only to find that the ugly and lengthy code is still almost
unchanged. It really surprised me. What I should do: pass tab info to the constructor / newInstance
method / Bundle of a tab/page Fragment so it is always known to the tab. In my opinion, loading of
tab info should happen \(be implemented\) inside the GroupDetailFragment \(autonomy\). Till now, I
still haven't find an elegant solution to it.