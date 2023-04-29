// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    // Define versions in a single place
    extra.apply {
        // Sdk and tools
        set("compileSdkVersion", 31)
        set("minSdkVersion", 27)
        set("targetSdkVersion", 31)

        // App dependencies
        set("appCompatVersion", "1.6.1")
        set("commonsCodecVersion", "1.15")
        set("constraintLayoutVersion", "2.1.4")
        set("coreTestingVersion", "2.1.0")
        set("coroutinesVersion", "1.4.2")
        set("espressoVersion", "3.4.0")
        set("fragmentVersion", "1.5.6")
        set("glideVersion", "4.14.2")
        set("gradleVersion", "7.3.1")
        set("gsonVersion", "2.9.1")
        set("junitVersion", "4.13.2")
        set("lifecycleVersion", "2.5.1")
        set("materialVersion", "1.6.0")
        set("navigationVersion", "2.5.3")
        set("okhttpLoggingVersion", "4.10.0")
        set("preferenceVersion", "1.2.0")
        set("retrofitVersion", "2.9.0")
        set("roomVersion", "2.5.1")
        set("runnerVersion", "1.0.1")
        set("swipeRefreshLayoutVersion", "1.1.0")
        set("truthVersion", "0.42")
        set("testExtJunit", "1.1.3")
        set("uiAutomatorVersion", "2.2.0")
        set("viewPagerVersion", "1.1.0-beta01")
        set("webkitVersion", "1.6.0")
        set("workVersion", "2.7.1")

    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${rootProject.extra.get("gradleVersion")}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${rootProject.extra.get("navigationVersion")}")

    }
}

tasks.register("clean").configure{
    delete(rootProject.buildDir)
}