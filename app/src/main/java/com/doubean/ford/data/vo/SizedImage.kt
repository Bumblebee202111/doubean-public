package com.doubean.ford.data.vo

import com.google.gson.annotations.SerializedName

data class SizedImage (
    @SerializedName("is_animated")
    val isAnimated: Boolean = false,
    val large: ImageItem? = null,
    val normal: ImageItem? = null,
    val raw: ImageItem? = null,
    val small: ImageItem? = null,
    val video: ImageItem? = null
)
    data class ImageItem (
        val height: Int = 0,
        val size: Long = 0,
        val url: String? = null,
        val width: Int = 0
    )
