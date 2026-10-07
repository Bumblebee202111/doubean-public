package com.github.bumblebee202111.doubean.data.doulist

import com.github.bumblebee202111.doubean.core.network.model.common.NetworkDouListItem
import com.github.bumblebee202111.doubean.core.network.model.common.NetworkDouLists
import com.github.bumblebee202111.doubean.core.network.model.common.toFeedItem
import com.github.bumblebee202111.doubean.core.network.model.doulists.NetworkDouList
import com.github.bumblebee202111.doubean.core.network.model.doulists.NetworkItemDouList
import com.github.bumblebee202111.doubean.core.network.model.doulists.NetworkItemDouLists
import com.github.bumblebee202111.doubean.core.network.model.doulists.NetworkMyCollectedItemDouList
import com.github.bumblebee202111.doubean.core.network.model.fangorns.toColorScheme
import com.github.bumblebee202111.doubean.core.network.model.toNonNullRating
import com.github.bumblebee202111.doubean.data.user.toUser
import com.github.bumblebee202111.doubean.model.doulist.DouList
import com.github.bumblebee202111.doubean.model.doulist.DouListItem
import com.github.bumblebee202111.doubean.model.doulist.DouListPostItem
import com.github.bumblebee202111.doubean.model.doulist.DouLists
import com.github.bumblebee202111.doubean.model.doulist.ItemDouList
import com.github.bumblebee202111.doubean.model.doulist.ItemDouLists
import com.github.bumblebee202111.doubean.model.doulist.MyCollectedItemDouList

fun NetworkDouList.toDouList(): DouList = DouList(
    id = this.id,
    title = this.title,
    uri = this.uri,
    alt = this.alt,
    type = this.type,
    sharingUrl = this.sharingUrl,
    coverUrl = this.coverUrl,
    intro = this.intro,
    isFollowed = this.isFollowed,
    playableCount = this.playableCount,
    createTime = this.createTime,
    owner = this.owner.toUser(),
    category = this.category,
    isMergedCover = this.isMergedCover,
    followersCount = this.followersCount,
    isPrivate = this.isPrivate,
    updateTime = this.updateTime,
    tags = this.tags,
    
    headerBgImage = this.headerBgImage,
    doulistType = this.doulistType,
    doneCount = this.doneCount,
    colorScheme = this.colorScheme?.toColorScheme(),
    itemCount = this.itemCount,
    isSysPrivate = this.isSysPrivate,
    listType = this.listType
)

fun NetworkDouListItem.toDouListPostItem(): DouListPostItem {
    val feedItem = toFeedItem()
    return DouListPostItem(
        feedItem = feedItem,
        collectionReason = collectionReason,
        collectionTime = collectionTime,
        douList = doulist?.toMyCollectedItemDouList()
    )
}


fun NetworkDouLists.toDouListPosts() = items.map { it.toDouListPostItem() }

fun com.github.bumblebee202111.doubean.core.network.model.doulists.NetworkDouListItem.toDouListItem(): DouListItem =
    DouListItem(
        id = id,
        type = type,
        createTime = createTime,
        uri = uri,
        targetId = targetId,
        title = title,
        subtitle = subtitle,
        coverUrl = coverUrl,
        comment = comment,
        rating = rating?.toNonNullRating()
    )

fun com.github.bumblebee202111.doubean.core.network.model.doulists.NetworkDouLists.toDouLists(): DouLists =
    DouLists(
        count = this.count,
        hasPlayLists = this.hasPlayLists,
        start = this.start,
        user = this.user.toUser(),
        douLists = this.douLists.map { it.toDouList() },
        hasPodcastLists = this.hasPodcastLists,
        total = this.total,
        hasReadLists = this.hasReadLists
    )

fun NetworkItemDouList.toItemDouList(): ItemDouList = ItemDouList(
    id = this.id,
    title = this.title,
    uri = this.uri,
    alt = this.alt,
    type = this.type,
    sharingUrl = this.sharingUrl,
    coverUrl = this.coverUrl,
    isFollowed = this.isFollowed,
    createTime = this.createTime,
    owner = this.owner.toUser(),
    category = this.category,
    isMergedCover = this.isMergedCover,
    followersCount = this.followersCount,
    isPrivate = this.isPrivate,
    updateTime = this.updateTime,
    
    doulistType = this.doulistType,
    doneCount = this.doneCount,
    itemCount = this.itemCount,
    isSysPrivate = this.isSysPrivate,
    listType = this.listType,
    isCollected = this.isCollected
)

fun NetworkItemDouLists.toItemDouLists(): ItemDouLists = ItemDouLists(
    count = this.count,
    start = this.start,
    total = this.total,
    douLists = this.douLists.map { it.toItemDouList() },
)

fun NetworkMyCollectedItemDouList.toMyCollectedItemDouList(): MyCollectedItemDouList =
    MyCollectedItemDouList(
        id = this.id,
        title = this.title,
        uri = this.uri,
        alt = this.alt,
        type = this.type,
        sharingUrl = this.sharingUrl,
        owner = this.owner.toUser(),
        category = this.category,
        
        doulistType = this.doulistType,
        listType = this.listType
    )