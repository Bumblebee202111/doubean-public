package com.github.bumblebee202111.doubean.feature.subjects.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.subjects.SubjectsScreen
import kotlinx.serialization.Serializable

@Serializable
data object SubjectsRoute

fun NavGraphBuilder.subjectsScreen(onSettingsClick: () -> Unit) {
    composable<SubjectsRoute> {
        SubjectsScreen(onSettingsClick)
    }
}