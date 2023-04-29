package com.doubean.ford.data.vo

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Entity(tableName = "groups")
class Group (
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
    val shortDescription: String? = null,

    @SerializedName("desc")
    val description: String? = null,

    @SerializedName("group_tabs")
    val tabs: List<GroupTab>? = null,

    @SerializedName("post_tags_normal")
    val postTags: List<GroupPostTag>? = null,

    @SerializedName("background_mask_color")
    val colorString: String? = null
)