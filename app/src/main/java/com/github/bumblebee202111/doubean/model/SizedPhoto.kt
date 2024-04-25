package com.github.bumblebee202111.doubean.model

import kotlinx.serialization.Serializable

@Serializable
data class SizedPhoto(
    val id: String,
    val description: String,
    val image: SizedImage,
    val origin: Boolean,
    val tag: String,
)

@Serializable
data class SizedImage(
    val isAnimated: Boolean = false,
    val large: ImageItem,
    val normal: ImageItem,
    val raw: ImageItem? = null,
    val small: ImageItem? = null,
    val video: ImageItem? = null,
)

@Serializable
data class ImageItem(
    val height: Int,
    val size: Long?,
    val url: String,
    val width: Int,
)