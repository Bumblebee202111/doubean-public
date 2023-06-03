package com.doubean.ford.api.model

import com.doubean.ford.data.db.model.UserEntity
import com.google.gson.annotations.SerializedName

data class NetworkUser(
    val id: String,

    @SerializedName("kind")
    val type: String,

    val name: String,

    @SerializedName("avatar")
    val avatarUrl: String,
)

fun NetworkUser.asEntity() = UserEntity(
    id = id, type = type, name = name, avatarUrl = avatarUrl
)