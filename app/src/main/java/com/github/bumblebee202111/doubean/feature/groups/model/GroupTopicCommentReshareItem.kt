package com.github.bumblebee202111.doubean.feature.groups.model

import com.github.bumblebee202111.doubean.model.fangorns.User
import java.time.LocalDateTime

data class GroupTopicCommentReshareItem(
    val id: String,
    val text: String,
    val createTime: LocalDateTime,
    
    val uri: String?,
    val author: User,
)