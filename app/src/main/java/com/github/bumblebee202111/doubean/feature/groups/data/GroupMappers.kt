package com.github.bumblebee202111.doubean.feature.groups.data

import com.github.bumblebee202111.doubean.data.db.model.CachedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPinnedTabItem
import com.github.bumblebee202111.doubean.feature.groups.model.GroupDetail
import com.github.bumblebee202111.doubean.feature.groups.model.GroupMemberRole
import com.github.bumblebee202111.doubean.feature.groups.model.GroupTab
import com.github.bumblebee202111.doubean.feature.groups.model.PinnedTabItem
import com.github.bumblebee202111.doubean.network.model.fangorns.NetworkGroupDetail
import com.github.bumblebee202111.doubean.network.model.fangorns.NetworkGroupTab

internal fun NetworkGroupDetail.toGroupDetail() = GroupDetail(
    id = id,
    name = name,
    memberCount = memberCount,
    topicCount = topicCount,
    sharingUrl = sharingUrl,
    url = url,
    uri = uri,
    avatar = avatar,
    memberName = memberName,
    createTime = createTime,
    description = description,
    tabs = tabs.map { it.toGroupTab() },
    color = backgroundMaskColor,
    memberRole = GroupMemberRole.of(memberRole),
    isSubscriptionEnabled = enableSubscribe,
    isSubscribed = isSubscribed
)


internal fun NetworkGroupTab.toGroupTab() = GroupTab(
    id = id, name = name, seq = seq
)

internal fun NetworkGroupDetail.toCachedGroupEntity() = CachedGroupEntity(
    id = id,
    name = name,
    avatar = avatar,
    url = url,
    uri = uri,
    color = backgroundMaskColor,
)

internal fun NetworkGroupTab.toGroupTabEntity(groupId: String) = GroupTabEntity(
    id = id,
    name = name,
    seq = seq,
    groupId = groupId
)

fun PopulatedPinnedTabItem.toPinnedTabItem() = PinnedTabItem(
    pinnedDate = pinnedDate,
    groupId = groupId,
    groupName = groupName,
    groupAvatar = groupAvatar,
    tabId = tabId,
    tabName = tabName
)