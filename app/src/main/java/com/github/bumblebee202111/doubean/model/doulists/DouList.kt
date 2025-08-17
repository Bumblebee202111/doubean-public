package com.github.bumblebee202111.doubean.model.doulists

import com.github.bumblebee202111.doubean.model.fangorns.BaseFeedableItem
import com.github.bumblebee202111.doubean.model.fangorns.ColorScheme
import com.github.bumblebee202111.doubean.model.fangorns.User

data class DouList(
    override val id: String,
    override val title: String,
    override val uri: String,
    override val alt: String,
    override val type: String,
    override val sharingUrl: String,
    override val coverUrl: String,
    val intro: String,
    val isFollowed: Boolean,
    val playableCount: Int,
    val createTime: String,
    val owner: User,
    val category: String,
    val isMergedCover: Boolean,
    val followersCount: Int,
    val isPrivate: Boolean,
    
    val updateTime: String,
    val tags: List<String>,
    
    
    val headerBgImage: String,
    val doulistType: String,
    val doneCount: Int,
    val colorScheme: ColorScheme?,
    val itemCount: Int,
    val isSysPrivate: Boolean,
    val listType: String,
) : BaseFeedableItem