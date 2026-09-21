package com.github.bumblebee202111.doubean.shared.user.model

import com.github.bumblebee202111.doubean.model.fangorns.HiddenTypeInProfile
import com.github.bumblebee202111.doubean.model.fangorns.ProfileImage
import java.time.LocalDateTime

data class UserDetail(
    val id: String,
    val uid: String,
    val name: String,
    val avatar: String,
    val registerTime: LocalDateTime,
    val movieCollectedCount: Int,
    val bookCollectedCount: Int,
    val musicCollectedCount: Int,
    val hasCommunityContribution: Boolean,
    val ipLocation: String?,
    val hometown: String?,
    val location: String?,
    val hiddenTypesInProfile: List<HiddenTypeInProfile>,
    val profileHidingReason: String,
    val intro: String,
    val profileBanner: ProfileImage,
)