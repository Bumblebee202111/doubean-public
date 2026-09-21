package com.github.bumblebee202111.doubean.shared.doulist.model

import com.github.bumblebee202111.doubean.shared.user.model.User

data class DouLists(
    val count: Int,
    val hasPlayLists: Boolean,
    val start: Int,
    val user: User,
    val douLists: List<DouList>,
    val hasPodcastLists: Boolean,
    val total: Int,
    val hasReadLists: Boolean,
)