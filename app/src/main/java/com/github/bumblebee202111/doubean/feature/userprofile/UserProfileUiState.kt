package com.github.bumblebee202111.doubean.feature.userprofile

import com.github.bumblebee202111.doubean.model.User
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class UserProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: UiMessage? = null,
    val isTargetingCurrentUser: Boolean = false,
    val isLoggedIn: Boolean = false,
)