package com.github.bumblebee202111.doubean.model.groups

data class SimpleGroupWithColor(
    val id: String,
    val name: String,
    val url: String,
    val uri: String,
    val avatar: String,
    val color: String?,
)