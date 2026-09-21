package com.github.bumblebee202111.doubean.feature.userprofile

import com.github.bumblebee202111.doubean.feature.userprofile.model.ProfileCommunityContribution
import com.github.bumblebee202111.doubean.shared.subject.model.MySubject
import com.github.bumblebee202111.doubean.shared.user.model.UserDetail
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