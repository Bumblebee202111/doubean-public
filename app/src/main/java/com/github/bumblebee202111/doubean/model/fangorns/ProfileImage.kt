package com.github.bumblebee202111.doubean.model.fangorns

data class ProfileImage(
    val color: String,
    val isDefault: Boolean,
    override val large: String,
    override val normal: String,
) : AbstractImage