Change Log
==========

## Version 0.5.2

- New: ImageScreen (view and save)
- Optimize: Port Timeline images composable to comment list
- Optimize: The single image of a list item is no longer cropped, instead it is usually shown with its original size and will only takes up to golden fraction of available size

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
