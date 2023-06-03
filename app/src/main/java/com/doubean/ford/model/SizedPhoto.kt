package com.doubean.ford.model

data class SizedPhoto(
    val id: String,
    val description: String,
    val image: SizedImage,
    val origin: Boolean,
    val tag: String,
)

data class SizedImage(
    val isAnimated: Boolean = false,
    val large: ImageItem,
    val normal: ImageItem,
    val raw: ImageItem? = null,
    val small: ImageItem? = null,
    val video: ImageItem? = null,
)

data class ImageItem(
    val height: Int,
    val size: Long,
    val url: String,
    val width: Int,
)