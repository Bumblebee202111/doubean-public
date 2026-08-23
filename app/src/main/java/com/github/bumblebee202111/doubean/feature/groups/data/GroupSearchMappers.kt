package com.github.bumblebee202111.doubean.feature.groups.data

import com.github.bumblebee202111.doubean.data.db.model.SimpleCachedGroupPartialEntity
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.network.model.search.NetworkGroupSearchResultGroupItem

fun NetworkGroupSearchResultGroupItem.toSimpleCachedGroupPartialEntity() =
    SimpleCachedGroupPartialEntity(
        id = id,
        name = name,
        avatar = avatar,
        url = url,
        uri = uri
    )

fun NetworkGroupSearchResultGroupItem.toGroupItemWithIntroInfo() =
    GroupItemWithIntroInfo(
        id = id,
        name = name,
        url = url,
        uri = uri,
        avatar = avatar,
        memberCount = memberCount,
        memberName = memberName,
        descAbstract = descAbstract
    )