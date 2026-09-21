package com.github.bumblebee202111.doubean.feature.userprofile.mapper

import com.github.bumblebee202111.doubean.core.network.model.profile.NetworkProfileCommunityContribution
import com.github.bumblebee202111.doubean.core.network.model.profile.NetworkProfileStatItem
import com.github.bumblebee202111.doubean.feature.userprofile.model.ProfileCommunityContribution
import com.github.bumblebee202111.doubean.feature.userprofile.model.ProfileStatItem

fun NetworkProfileCommunityContribution.toProfileCommunityContribution() =
    ProfileCommunityContribution(
        items = items.map(NetworkProfileStatItem::toProfileStatItem)
    )


fun NetworkProfileStatItem.toProfileStatItem() = ProfileStatItem(
    title = title,
    total = total,
    type = type,
    uri = uri
)