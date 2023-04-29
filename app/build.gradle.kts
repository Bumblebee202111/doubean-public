plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk=33
    buildFeatures {
        dataBinding = true
    }
    defaultConfig {
        applicationId = "com.doubean.ford"
        minSdk=27
        targetSdk=31
        versionCode=1
        versionName="0.4.0"
        setProperty("archivesBaseName", "doubean_$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "DOUBAN_ACCESS_KEY", "\"" + getDoubanAccess() + "\"")
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += "room.schemaLocation" to "$projectDir/schemas"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation ("androidx.appcompat:appcompat:${rootProject.extra.get("appCompatVersion")}")
    implementation ("androidx.constraintlayout:constraintlayout:${rootProject.extra.get("constraintLayoutVersion")}")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:${rootProject.extra.get("lifecycleVersion")}")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:${rootProject.extra.get("lifecycleVersion")}")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:${rootProject.extra.get("lifecycleVersion")}")
    implementation ("androidx.navigation:navigation-fragment:${rootProject.extra.get("navigationVersion")}")
    implementation ("androidx.navigation:navigation-ui:${rootProject.extra.get("navigationVersion")}")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:${rootProject.extra.get("swipeRefreshLayoutVersion")}")
    implementation ("androidx.preference:preference-ktx:${rootProject.extra.get("preferenceVersion")}")
    implementation ("androidx.room:room-runtime:${rootProject.extra.get("roomVersion")}")
    implementation ("androidx.webkit:webkit:${rootProject.extra.get("webkitVersion")}")
    implementation ("androidx.work:work-runtime:${rootProject.extra.get("workVersion")}")
    implementation ("androidx.viewpager2:viewpager2:${rootProject.extra.get("viewPagerVersion")}")
    implementation ("commons-codec:commons-codec:${rootProject.extra.get("commonsCodecVersion")}")
    implementation ("com.github.bumptech.glide:glide:${rootProject.extra.get("glideVersion")}")
    implementation ("com.github.zjupure:webpdecoder:2.3.${rootProject.extra.get("glideVersion")}")
    implementation ("com.google.android.material:material:${rootProject.extra.get("materialVersion")}")
    implementation ("com.google.code.gson:gson:${rootProject.extra.get("gsonVersion")}")
    implementation ("com.squareup.retrofit2:converter-gson:${rootProject.extra.get("retrofitVersion")}")
    implementation ("com.squareup.retrofit2:retrofit:${rootProject.extra.get("retrofitVersion")}")

    kapt ("androidx.room:room-compiler:${rootProject.extra.get("roomVersion")}")

    annotationProcessor ("com.github.bumptech.glide:compiler:${rootProject.extra.get("glideVersion")}")

    testImplementation ("junit:junit:${rootProject.extra.get("junitVersion")}")
    androidTestImplementation ("androidx.test.espresso:espresso-core:${rootProject.extra.get("espressoVersion")}")
}

fun getDoubanAccess ():String? {
    return project.findProperty("douban_access_key")as? String
}