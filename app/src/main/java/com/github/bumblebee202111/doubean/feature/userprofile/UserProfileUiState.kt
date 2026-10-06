package com.github.bumblebee202111.doubean.feature.userprofile

import com.github.bumblebee202111.doubean.model.subject.MySubject
import com.github.bumblebee202111.doubean.model.user.ProfileCommunityContribution
import com.github.bumblebee202111.doubean.model.user.UserDetail
import com.github.bumblebee202111.doubean.ui.model.UiMessage

data class UserProfileUiState(
    val isLoading: Boolean = false,
    val user: UserDetail? = null,
    val communityContribution: ProfileCommunityContribution? = null,
    val profileSubjects: List<MySubject>? = null,
    val errorMessage: UiMessage? = null,
    val isTargetingCurrentUser: Boolean = false,
    val isLoggedIn: Boolean = false,
)