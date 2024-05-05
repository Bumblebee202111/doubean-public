Change Log
==========

## Version 0.5.5

- New: Support displaying GIFs with animations (only the "large" quality of a douban gif is animated, which can be viewed in ImageScreen)
- New: Show IP locations of posts & comments
- Fix: Topic detail: Issue of topic content flickering solved
- Fix: WorkManager: Posts feed can function normally now
- Optimize: Topic detail
  - Comments are loaded with Ktor/Paging 3
  - Introduce Compose
  - Optimize details
- Optimize: Starting tab 
  - Toggling setting won't setup navigation again 
  - Remove unnecessary icon animation when starting with groups tab
- Optimize: Clean up unnecessary dependencies
- Known issue: Topic WebView scroll position tends to be totally reset when you are returning from comment list / other screens

## Version 0.5.4

- New: Topic content WebView: Support viewing topic polls and questions
- New: Topic content WebView: Use official styles for topic content
- Change: Topic content WebView: Tap to view an image rather than long click to save it

## Version 0.5.3

- New: Topic content WebView: Link clicks are intercepted to function properly.
- New: Topic content WebView: Long press an image to save
- New: Topic content WebView: Utilize official stylesheets and enable viewport
  - Now images no longer overflow and embedded players are properly zoomed

- New: Support WebView dark mode for Android versions ≥ T

## Version 0.5.2

- New: ImageScreen (view and save)
- Optimize: Port Timeline images composable to comment list
- Optimize: The single image of a list item is no longer cropped, instead it is shown with its original size by default. But if it is too large, it will fit to the golden fraction of the size of available and visible space.

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
