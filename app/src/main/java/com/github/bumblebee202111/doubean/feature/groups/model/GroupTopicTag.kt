package com.github.bumblebee202111.doubean.feature.groups.model

import androidx.room.PrimaryKey

data class GroupTopicTag(
    @PrimaryKey
    val id: String,
    val name: String,
)