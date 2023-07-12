package com.doubean.ford.api.model

import com.doubean.ford.model.ImageItem
import com.doubean.ford.model.SizedImage
import com.doubean.ford.model.SizedPhoto
import com.google.gson.annotations.SerializedName

data class NetworkSizedPhoto(
    val id: String,
    val description: String,
    val image: NetworkSizedImage,
    val origin: Boolean,
    @SerializedName("tag_name")
    val tag: String,
)

data class NetworkSizedImage(
    @SerializedName("is_animated")
    val isAnimated: Boolean = false,
    val large: NetworkImageItem,
    val normal: NetworkImageItem,
    val raw: NetworkImageItem? = null,
    val small: NetworkImageItem? = null,
    val video: NetworkImageItem? = null,
)

data class NetworkImageItem(
    val height: Int,
    val size: Long,
    val url: String,
    val width: Int,
)

fun NetworkSizedPhoto.asEntityAndExternalModel() = SizedPhoto(
    id = id,
    description = description,
    image = image.asEntityAndExternalModel(),
    origin = origin,
    tag = tag
)

fun NetworkSizedImage.asEntityAndExternalModel() = SizedImage(
    isAnimated = isAnimated,
    large = large.asEntityAndExternalModel(),
    normal = normal.asEntityAndExternalModel(),
    raw = raw?.asEntityAndExternalModel(),
    small = small?.asEntityAndExternalModel(),
    video = video?.asEntityAndExternalModel()
)

fun NetworkImageItem.asEntityAndExternalModel() = ImageItem(
    height = height, size = size, url = url, width = width
)