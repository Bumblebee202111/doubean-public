package com.github.bumblebee202111.doubean.feature.userprofile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.userprofile.UserProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileRoute(val userId: String? = null)

fun NavGraphBuilder.userProfileScreen(
    onSettingsClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) =
    composable<UserProfileRoute> {
        UserProfileScreen(onSettingsClick = onSettingsClick, onLoginClick = onLoginClick)
    }

fun NavController.navigateToUserProfile(userId: String? = null) =
    navigate(route = UserProfileRoute(userId))
