

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
}

tasks.register("clean").configure{
    delete(rootProject.layout.buildDirectory)
}

buildscript {
    dependencies {
    }
}
