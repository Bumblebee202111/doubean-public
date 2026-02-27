package com.github.bumblebee202111.doubean.feature.userprofile.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.userprofile.UserProfileScreen
import com.github.bumblebee202111.doubean.feature.userprofile.UserProfileViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileNavKey(val userId: String? = null) : NavKey

fun EntryProviderScope<NavKey>.userProfileEntry(
    onStatItemUriClick: (uri: String) -> Boolean,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) =
    entry<UserProfileNavKey> { key ->
        UserProfileScreen(
            onStatItemUriClick = onStatItemUriClick,
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onLoginClick = onLoginClick,
            viewModel = hiltViewModel<UserProfileViewModel, UserProfileViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.userId)
                }
            )
        )
    }

fun Navigator.navigateToUserProfile(userId: String? = null) =
    navigate(key = UserProfileNavKey(userId))
