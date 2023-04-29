package com.doubean.ford.data.vo

import com.google.gson.annotations.SerializedName

class SizedPhoto (
    val id: String,
    val description: String? = null,
    val image: SizedImage? = null,
    val origin: Boolean = false,
    @SerializedName("tag_name")
    val tag: String? = null
)