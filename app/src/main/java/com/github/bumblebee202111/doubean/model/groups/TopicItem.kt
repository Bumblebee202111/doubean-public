package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.model.User
import java.time.LocalDateTime

data class TopicItem(
    override val id: String,
    override val title: String,
    override val author: User,
    override val createTime: LocalDateTime,
    override val updateTime: LocalDateTime,
    override val commentsCount: Int,
    override val tags: List<GroupTopicTag>,
    override val coverUrl: String?,
    override val url: String,
    override val uri: String,
) : AbstractTopicItem

fun TopicItem.withGroup(groupItem: GroupItem): TopicItemWithGroup {
    return TopicItemWithGroup(
        id = id,
        title = title,
        author = author,
        createTime = createTime,
        updateTime = updateTime,
        commentsCount = commentsCount,
        tags = tags,
        coverUrl = coverUrl,
        url = url,
        uri = uri,
        group = groupItem
    )
}