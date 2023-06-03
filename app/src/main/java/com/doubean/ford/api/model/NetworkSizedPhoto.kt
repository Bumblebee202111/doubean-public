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

fun NetworkSizedPhoto.asExternalModel() = SizedPhoto(
    id = id, description = description, image = image.asExternalModel(), origin = origin, tag = tag
)

fun NetworkSizedImage.asExternalModel() = SizedImage(
    isAnimated = isAnimated,
    large = large.asExternalModel(),
    normal = normal.asExternalModel(),
    raw = raw?.asExternalModel(),
    small = small?.asExternalModel(),
    video = video?.asExternalModel()
)

fun NetworkImageItem.asExternalModel() = ImageItem(
    height = height, size = size, url = url, width = width
)