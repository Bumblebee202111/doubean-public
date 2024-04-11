@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    //alias(libs.androidx.navigation.ui.safe.args.gradle.plugin)
    alias(libs.plugins.kapt)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildFeatures {
        dataBinding = true
    }
    defaultConfig {
        applicationId = "com.doubean.ford"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.5.0"
        setProperty("archivesBaseName", "doubean_$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "DOUBAN_ACCESS_KEY", "\"" + getDoubanAccess() + "\"")
        javaCompileOptions {
            annotationProcessorOptions {
                //https://developer.android.google.cn/jetpack/androidx/releases/room#compiler-options
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.webkit)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.viewpager2)
    implementation(libs.commons.codec)
    implementation(libs.material)
    implementation(libs.glide)
    kapt(libs.glide.compiler)
    implementation(libs.webpdecoder)
    implementation(libs.gson)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)

    //Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}

fun getDoubanAccess(): String? {
    return project.findProperty("douban_access_key") as? String
}