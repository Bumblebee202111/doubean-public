doubean
=======

An unofficial [Douban][douban] app mainly used for browsing the [Groups][douban-groups] section.
\([豆瓣][douban]非官方app，主要用于[小组][douban-groups]浏览。\)

From now on, this repo will not be updated frequently as I am getting tired and bored of its
development as the app grows larger. Note I am an inexperienced beginner and the only developer of
it. Occasional crashes may happen, UI design may be incomplete and features you need may be missing.
Pretty much of my work is copy and paste from the official documentation and samples of Google
Android. I am trying my best only to ensure that the app will function normally on my Xiaomi devices
with Android version varying from 8.1 to 12.

[douban]: https://www.douban.com/

[douban-groups]: https://www.douban.com/group/

Introduction
------------

### Functionality

The app is composed of 3 main screens, two of which are blank and left for future development.

#### GroupFragment

##### Groups Home

* Following groups and tabs

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

To not get myself into trouble, I have added detailed implementation of accessing data to .gitignore
list. However, the latest release is always available.

### Who is it for?

* Me and other developers who are learning Android, Java and version control
* Me and other users who want easier access to Douban groups

### Non-Goals

* Login-based features
* Kotlin
* Dagger2/Hilt
* RxAndroid
* Heavy use of Material Design

### Features

It not only implements the very basic features of Douban app, but also has its own features, some of
which react to the trend of Douban community to being unfriendly and closed to non-members. Examples
are abuse of power, "apply to follow or to talk" and etc.

* Inherent advantages leading to good performance and simple coding | Design Aspect | Choice\(s\) |
  | --- | --- | | Language | Java | | Libraries | Jetpack and authoritative third party libraries |
  | Architecture | MVVM | | Design philosophy | Android Jetpack |
* Support for viewing content in Douban WebView optimized for mobile reading
* Support for loading cache as alternative when connection is off
* Bilingual support for UI strings
* Ad-free, lite \(~30MB\)
* Support URL deep links
* Basic support for dark theme and tablets/landscape
* Basic paging support
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

* \[GroupsHomeFragment\]Update default follows
* \[GroupFragment\]\[GroupPostFragment\]Share
* \[GroupFragment\]Post order: date created \(not real cuz no such API\), last updated and top
* \[GroupFragment\]\[GroupPostFragment\]Open in browser/Douban app
* \[GroupsHomeFragment\]Add "groups of the day" list
* SnackbarUtils from todo app
* \[GroupFragment\] Update implementation of CollapsingToolBar – do not use the Sunflower logic
  * 🛠 Title not shown on recreate
* Rate limit
* \[GroupsHomeFragment\]Show groupFollow / saved posts with group info
* \[GroupFragment\]Adjust when to hide/show toolbar
* \[GroupPostFragment\]Save WebView images
* \[GroupFragment\]Search group/tab posts
* \[GroupsFragment\]Search posts of all groups
* Use Twitter-like date formatting
* \[Lists\]Allow follow/save for each item
* \[GroupFragment\]\[GroupPostFragment\]Track/mark/revert read
* \[GroupsHomeFragment\]Unfollow/reorder items
* \[GroupFragment\]WebView for group
* \[GroupFragment\]Refactor following, support following groups, tags and search which can
  constitute custom posts categories/groups with filters available
* \[GroupFragment\]\[GroupPostFragment\]Save various types of searches, posts and comments
* \[WebView\]Complete CSS of content WebView: find official CSS source or write on my own
* \[GroupFragment\]Group/tab/post shortcuts
* \[Lists\]Reddit-like item expand/collapse
* \[GroupFragment\]For post items, optimize tag display, e.g., assign color to each tag mapped from
  name
* 🛠\[GroupPostFragment\]Dealing with post URL click - new page should not show in WebView
* Support blocking unfriendly content
* \[GroupsHomeFragment\]Recommendations using SharedPref
* \[Lists\]Post order: custom rules taking multiple conditions into account
* Widgets
* Notifications and push services \(delayed in consideration of its difficulty and unimportance\)
* 🛠 Views on top of WebView become invisible after scroll on my MIUI 10 Android 8.1 device
  \(reasons unknown, may never be fixed\)
* Uploads copies of responses to some place and reuses it among users to prevent frequent Douban
  visits.
* Other modules, e.g., books

General goals:

* Color
* Typography
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
