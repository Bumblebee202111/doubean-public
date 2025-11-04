package com.github.bumblebee202111.doubean.model.doulists

import com.github.bumblebee202111.doubean.model.fangorns.User

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