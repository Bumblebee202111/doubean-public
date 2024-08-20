Change Log
==========

## Version 0.6.3(603)

## Version 0.6.2(602)

- Add some Chinese translations
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
