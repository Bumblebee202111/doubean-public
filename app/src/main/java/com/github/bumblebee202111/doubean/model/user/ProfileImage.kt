package com.github.bumblebee202111.doubean.model.user

import com.github.bumblebee202111.doubean.model.common.AbstractImage

data class ProfileImage(
    val color: String,
    val isDefault: Boolean,
    override val large: String,
    override val normal: String,
) : AbstractImage