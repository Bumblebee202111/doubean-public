Change Log
==========

## Version 0.9.0-beta2(90002)

- Support viewing *list* of a user's created Dou Lists

## Version 0.9.0-beta1(90001)

**Me / User Profile Screen Updates**

- Enhanced profiles: See more details with an updated profile header design. (Now includes Douban contributions!)
- View others: Tap user names or avatars to visit their profiles.
- "Me" tab: Your main profile tab is now called "Me."

## Version 0.8.5(805)

- Improve & refactor snackbar message display
  - Introduce SnackbarManager
  - Support displaying error messages via SnackBar for almost every network fetch
  - Messages can now survive screen navigation
- Fix group sub button not immediately refreshed
- Update dependencies

## Version 0.8.4(804)

- Topic
  - Support react (vote) actions & viewing collection state (preparation for topic collecting support)
  - Display edit time (if available) with animation
  - Optimize code

- Simplify caching of the group entity
- Fix Tab component min width for Group Getail

## Version 0.8.3(803)

- Add Subject Unions module support for Subjects tab
- Update subject search
  - Implement official style (dynamic type/tag support)
  - Remove individual tab entrances  
  - Improve error message handling
- Fix blank trailing comments for Topic by stop using placeholders
  - Cause: Actual # comments in a page response can not be calculated early

## Version 0.8.2(802)

- Make system navigation bar transparent (#9)
- Fix content padding of Group Tab & Settings
  - Content now scrolls over system navigation bar
- Fix login captcha
  - Problems might still occur
- Auto clear bad saved session

## Version 0.8.1(801)

- Login updates
  - Fixed frequent login failures
  - Added clearer error messages
  - Implemented captcha support *(not fully tested — captcha prompts stopped appearing during development)*

- Notification updates
  - Fixed text color in dark mode for group/tab notification dialogs
  - Preference of max topics (queried) per fetch -> max topic *notifications* per fetch
    - Local create time orders now bring more relevant & less frequent notifications

  - Fixed broken notification taps by simply updating the Navigation library *(took all afternoon – thanks, bug!)*


## Version 0.8.0(800)

- Added experimental phone/password login
  ⚠️ *Warning*:
  → TOO BUGGY, DON'T USE
  → Next release: Captcha :eyes::eyes:
  ~~→ Code remains unoptimized (high failure rate)
  → Frozen development to avoid developer account restrictions
  → Use may trigger platform safeguards~~  
  
- Fixed manual session imports

- Fixed WebView not darkening in system dark theme (fix #7)

## Version 0.7.17(717)

- Introduced a better way of handling errors
  - Parse error bodies
  - Show Snackbar messages
  - Currently only applied to subject tab modules (rank lists)
- Updated session handling: Tweak UI text, add session controls
  - The two main bullets above are also preparations for another attempt to implement p/p login
- Code rearrangements, cleanups & optimizations
- Fix TV rank nullability

## Version 0.7.16(716)

- Added support for more user interests (vertical pagination below status sections)
  - Not well tested since I don't have long interest lists

- Eliminated AndroidX Preferences along with AppCompat/Fragment
  - APK size reduced by around 1MB
- Converted RatingBar to Compose with precise star values

## Version 0.7.15(715)

- Improved organization of subject review counts
- Updated simple interest button to use a subtler `FilledTonalButton` for softer appearance
- Updated spinner implementation to use `ExposedDropDownMenuBox`
- Added tap-to-view for cover/trailer images in subject screens (fix #6)
- Reviews sheet can now be expanded to max height
- Optimized styles for various counts
- Optimized group tab action icons
- Fixed subtle issues of group/tab notifications button & tab favorite button
- Fix: Popular comments are cleared when loading non-first pages

## Version 0.7.14(714)

- Add pagination for RankList
- Set status bar to transparent and enforce light icons for group-themed screens
- Fix issues for TopicDetail header
  - Add basic support for special photo lists outside the content WebView 
  - Fix scrolling into content WebView from off-screen is unsmooth
  - Set background of content WebView to transparent

## Version 0.7.13(713)

- Update ProfileScreen
  - Display current user info
  - Add Settings button
  - Optimize component styles
- Add top-left avatars & remove Settings buttons for bottom tabs other than Profile
- Fix topic order mistake when calculating topics to notify
- Minor tweaks and optimizations

## Version 0.7.12(712)

- Fix: The feature of group topic notifications is finally restored after extensive code refactoring
- Fix: `ExpandCollapseText` now fills max width
- Fix: Restore padding in DoubeanWebView in WebViewScreen
- Fix: Fixed header group info alignment in Topic Detail
- Migration to Compose
  - Topic detail header & Notifications dialog are migrated to Compose
  - Removed/deprecated components:
    - View Data Binding
    - Android Spinner
    - Layout/menu XML files
    - Content of `styles.xml`

## Version 0.7.11(711)

- Migrate group topic item layouts to Compose MD3
- Migrate core layout of group detail from AppBarLayout/CollapsingToolbarLayout to Compose Scaffold/TwoRowsTopAppBar
  - Allow expanding group descriptions
  - Fix the severe long-existing bug that the last vertical scroll position of selected tab in Group Detail Screen is always lost (reset) when returned from Topic Detail Screen
- Now compiles against API 35

## Version 0.7.10(710)

- Correctly distinguish a topic tab and a topic tag in data received. This used to cause some problems.
- Fix subject parsing error
- Migrate layouts of group list items to Compose
- Minor tweaks

## Version 0.7.9(709)

- Add rank lists module in replacement of single T250 in each subjects tab
  - Add TVs tab for it
  - Generalize T250 UI to rank lists and add a separate RankListScreen
- Type labels are also shown in movie/tv search results
  - Updated label color
- Update icon for 'doing' interest status
- Tweak subject layouts
  - Also apply rounded corners to all subject covers
- 'Null' ratings are correctly shown with the reasons
- Groups: (Quickly) fix comment like icon and image area sometimes disappearing when entering from bottom
  * This fix might make the list laggy
  * The comment item layout needs full Compose migration
- Fix crash caused by duplicate keys in celebrity list
- Fix nullability of rating of subject interests

## Version 0.7.8(708)

- Add basic reviews bottom sheet to subject screens
- Fix trailers not having same height
- Fix improper (vertical) alignment of interest button content
- Fix interest button in collection subject items not being shown for logged-in users
- Fix non-existent photo id not being accepted for status type 'note'
- Apply abbreviated style to status dates to prevent potential overflow of the row in most devices
- While Statuses tab is in maintenance, these two bugs are annoying and their fixes are easy

## Version 0.7.7(707)

- Subject screens: support more details
  - Intro, interests, celebrities, trailers...
  - Better meta info overview
  - Tweak layout
- Use AnnotatedString for login prompts in Subjects
  - Show login prompt instead of interest buttons in subject detail for guests
- Add back button to Login app bar
- Fix #5 group searches (感谢反馈@wha4up)
- Fix subject searches

## Version 0.7.6(706)

- Add subject detail screens
  - Adjust list item click actions and place of view-in-Douban entrances for subjects
  - Not very detailed yet, but these are *new* screens anyway
- Show book subtitle in all book forms including list items
- Tweak SubjectInterestButtons
- Fix bottom navigation being initialized twice
- Known issue:
  - Interest buttons are not hidden in subject screens for guests

## Version 0.7.5(705)

- Add support for searching subjects
  - Pagination is not planned here
- Fix nullability issues in status type review which prevents data from being successfully parsed & then presented

## Version 0.7.4(704)

- Support clicking to view subjects in Douban
- Formally mark Statuses tab as maintained only
  - Add/update text hints

## Version 0.7.3(703)

- Support subject interest actions
  - This include plenty of adjustments to subject-related data models
- Optimize hints of LoginScreen

## Version 0.7.2(702)

- Support browsing my subjects (我的书影~~音~~)! 
  - Also no pagination yet
  - User actions are not ready either, although they will soon be available
- Tweak LoginScreen

## Version 0.7.1(701)

- Add **Top 250 Books** of **Books** of **Subjects (书影音)** (also no pagination yet)
- Fix issue where the star group of a rating bar is distorted in an obscure way when the rating is not multiple of 0.5 due to the built-in padding between stars by increasing step size to exactly this value (like Douban app), so that the stars is always rounded to multiple of it
- Code cleanups and minor tweaks

## Version 0.7.0(700)

- Add **Top 250 Movies** of **Movies** of **Subjects (书影音)**
  - No pagination yet, only first page is available
- Update topics sortby
  - Fix selected item of sort topics by spinner not being remembered
  - Update terminology to match that of Douban
  - Add "Hot (last created)" which sorts the fetched hot topics by created locally for each page
- Code cleanups and optimizations

## Version 0.6.4(604)

- Move Notifications from NavigationBar to Groups Home app bar
- "Home" -> "Statuses" (because it is not necessarily the "home" tab)
- Fix NavigationBarItem selection not shown
- Groups Home: Fix menu not being closed immediately on Settings click
- Code optimizations

## Version 0.6.3(603)

- Groups Home: Fix translation mistakes
- Migrate Fragment Navigation to Compose Navigation
  - Remove shell Fragments
  - Only usages of Fragment: PreferenceFragmentCompat
- Migrate most usages of MaterialToolBar to Compose TopAppBar
  - Reduce TopAppBar height to 56 dp

- Minor tweaks and optimizations

## Version 0.6.2(602)

- Add some Chinese translations
  - **Known issue: some titles of Groups Home are not correctly translated**

- Update date & time
  - Fully migrated into Compose
  - Add new format for date-time of yesterday for topic items: `Yesterday HH:mm`
- Fix initial scroll position of Groups Home (the previous fix does not work) 
- Code optimizations and cleanups

## Version 0.6.1(601)

- Support subscribing (关注) groups
  - Adjust favorite buttons
  - No longer show notifications buttons (the feature of topic notifications is currently broken)

- Update some MD2 icons to MD3
- "My Groups" data is now cached
  - ~~Fixes initial scroll position of Groups Home~~
  - Makes UX smoother

- Fix app crash due to "topic not found"
- Code optimizations

## Version 0.6.0(600)

- Kotlin 2.0 & K2 Compiler!
- Update launcher icon to use same background as the one of Douban app and text font Jebrains Mono
- Fix Groups Home crash due to ridiculously non-sense API change. 
- Optimize Groups Home and Profile/Login UI
- Slight adjustments and code optimizations

## Version 0.5.10

- For logged-in users, the new "my topics" section is shown in GroupsHomeScreen, replacing "groups of the day" section
  - They can still browse "Groups of the day" section in GroupsSearchScreen when the query is empty
- Fix a ROOM typo which may cause crash: `@Update` -> `@Upsert`
- Migrate DialogFragment/EditText/RecyclerView to Compose couterparts
- Code optimizations and cleanups

## Version 0.5.9

- ImageScreen: Support transform gestures
- Groups: Home My Favorites area will be hidden if you have no favorites
- Groups: Rename old local "follow" to "favorite"
- Groups: Screens of tab/default notifications preferences are temporary disabled due to some incompatibility between View system and Compose
- Remove unnecessary top padding of topic items
- Support 64-bit devices only
- Fix nullability of `create_time` of topic group (closes #2)

## Version 0.5.8

- Group detail rework. It's now much smoother.
- Group tab / group search are rewritten with Paging3
- Complete migrations into Ktor, Coil Compose, Pager, Paging3 and Flow
  - Remove dependencies of Retrofit, Glide, ViewPager2, LiveData
- Simplify network resource fetching and no longer use custom paging
- Fix comment images not being reset when comment index changes
- Other code optimizations 

## Version 0.5.7

- New: Topic detail: Allow viewing topic reshares
- Optimize: Code cleanups and optimizations

## Version 0.5.6

- New: Topic detail: Jump to comment of index feature
  - Add menu item to open seeker dialog for jumping to any index of comments
  - Comments are no longer cached
- Optimization: Topic detail: Hide sort comments by spinner if there are no popular comments
- Optimization: List item image groups: To better support jumping to bottom comments, single item image groups are initially sized with given metrics before the image is loaded

## Version 0.5.5

- New: Support displaying GIFs with animations (only the "large" quality of a douban gif is animated, which can be viewed in ImageScreen)
- New: Show IP locations of posts & comments
- Fix: Topic detail: Issue of topic content making other area flicker solved
- Fix: WorkManager: Posts feed can function normally now
- **Known issue**: Topic WebView scroll position tends to be totally reset when you are returning from comment list / other screens
- Optimize: Topic detail
  - Comments are loaded with Ktor/Paging 3
  - Introduce Compose
  - Optimize details
- Optimize: Starting tab 
  - Toggling setting won't setup navigation again 
  - Remove unnecessary icon animation when starting with groups tab
- Optimize: Clean up unnecessary dependencies


## Version 0.5.4

- New: Topic content WebView: Support viewing topic polls and questions
- New: Topic content WebView: Use official styles for topic content
- Change: Topic content WebView: Tap to view an image rather than long click to save it

## Version 0.5.3

- New: Topic content WebView: Link clicks are intercepted to respond properly.
- New: Topic content WebView: Long press an image to save
- New: Topic content WebView: Utilize official stylesheets and enable viewport
  - Now images no longer overflow and embedded players are properly zoomed

- New: Support WebView dark mode for Android versions ≥ T

## Version 0.5.2

- New: ImageScreen (view and save)
- Optimize: Port Timeline images composable to comment list
- Optimize: The single image of a list item is no longer cropped, instead it is shown with its original size by default. But if it is too large, it will fit to the golden fraction of the width of available and visible space.

## Version 0.5.1

- New: Home - Following

## Version 0.5.0

* Repo change: `doubean` -> `doubean-public`
  - New code will only be pushed into the private `doubean`
* ApplicationId change: Now `com.github.bumblebee202111.doubean`
* SDK change: Now compile against and targeting to SDK 34 (Android 14.0 UpsideDownCake)
* New: Rooted users can synchronize login status from Douban app
  * Even if manual login with existing Douban session (its access usually requires root) is available, it is not well supported and highly discouraged
* New: Introduce tons of libraries, like Jetpack Compose, Ktor, Kotlinx Serialization, Coil Compose, Compose Material 3, Daggaer Hilt and libsu.
* New: Groups home largely rewritten with these libraries.
