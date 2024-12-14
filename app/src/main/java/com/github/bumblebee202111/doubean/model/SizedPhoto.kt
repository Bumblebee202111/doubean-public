package com.github.bumblebee202111.doubean.model

import kotlinx.serialization.Serializable

@Serializable
data class SizedPhoto(
    val id: String,
    val description: String,
    val image: SizedImage,
    val origin: Boolean?,
    val tag: String,
)

@Serializable
data class SizedImage(
    val large: ImageItem,
    val normal: ImageItem,
)

@Serializable
data class ImageItem(
    val height: Int,
    val size: Long?,
    val url: String,
    val width: Int,
)