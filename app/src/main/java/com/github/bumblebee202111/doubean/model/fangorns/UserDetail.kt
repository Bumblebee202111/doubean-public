package com.github.bumblebee202111.doubean.model.fangorns

import java.time.LocalDateTime

data class UserDetail(
    val id: String,
    val uid: String,
    val name: String,
    val avatar: String,
    val registerTime: LocalDateTime,
    val movieCollectedCount: Int,
    val bookCollectedCount: Int,
    val hasCommunityContribution: Boolean,
    val ipLocation: String?,
    val hometown: String?,
    val location: String?,
    val hiddenTypesInProfile: List<HiddenTypeInProfile>,
    val profileHidingReason: String,
    val intro: String,
    val profileBanner: ProfileImage,
)