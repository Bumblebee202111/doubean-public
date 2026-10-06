package com.github.bumblebee202111.doubean.model.group

import androidx.room.PrimaryKey

data class GroupTopicTag(
    @PrimaryKey
    val id: String,
    val name: String,
)