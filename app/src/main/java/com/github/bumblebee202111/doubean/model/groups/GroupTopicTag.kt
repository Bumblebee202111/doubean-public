package com.github.bumblebee202111.doubean.model.groups

import androidx.room.PrimaryKey

data class GroupTopicTag(
    @PrimaryKey
    val id: String,
    val name: String,
)