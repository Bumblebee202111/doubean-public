package com.doubean.ford.data.vo

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    @SerializedName("kind")
    val type: String,
    val name: String ,

    @JvmField
    @SerializedName("avatar")
    val avatarUrl: String? = null
)