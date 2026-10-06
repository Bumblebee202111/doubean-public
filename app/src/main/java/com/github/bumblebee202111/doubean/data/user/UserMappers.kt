package com.github.bumblebee202111.doubean.data.user

import com.github.bumblebee202111.doubean.core.network.model.fangorns.NetworkUserDetail
import com.github.bumblebee202111.doubean.core.network.model.fangorns.toProfileImage
import com.github.bumblebee202111.doubean.data.db.model.UserEntity
import com.github.bumblebee202111.doubean.model.fangorns.HiddenTypeInProfile
import com.github.bumblebee202111.doubean.model.user.User
import com.github.bumblebee202111.doubean.model.user.UserDetail

fun NetworkUserDetail.toUserDetail() = UserDetail(
    id = id,
    uid = uid,
    name = name,
    avatar = avatar,
    registerTime = registerTime,
    movieCollectedCount = movieCollectedCount,
    bookCollectedCount = bookCollectedCount,
    musicCollectedCount = musicCollectedCount,
    hasCommunityContribution = hasCommunityContribution,
    ipLocation = ipLocation,
    hometown = hometown?.name,
    location = location?.name,
    hiddenTypesInProfile = hiddenTypesInProfile.map(HiddenTypeInProfile::fromString),
    profileHidingReason = profileHidingReason,
    intro = intro,
    profileBanner = profileBanner.toProfileImage(),
)

fun UserEntity.toUser() = User(
    id = id, uid = uid, name = name, avatar = avatar, uri = uri, alt = url
)