package com.doubean.ford.data.vo

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class GroupItem (
    @PrimaryKey
    val id: String,

    val name: String,

    @SerializedName("member_count")
    val memberCount: Int? = null,

    @SerializedName("topic_count")
    val postCount: Int? = null,

    @SerializedName("create_time")
    val dateCreated: LocalDateTime? = null,

    @SerializedName("sharing_url")
    val url: String,

    val uri: String,

    @SerializedName("avatar")
    val avatarUrl: String? = null,

    @SerializedName("member_name")
    val memberName: String? = null,

    @SerializedName("desc_abstract")
    val shortDescription: String? = null
)