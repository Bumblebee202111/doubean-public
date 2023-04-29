package com.doubean.ford.data.vo

import android.graphics.Color
import android.text.TextUtils
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class GroupBrief (
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("member_count")
    val memberCount: Int = 0,

    @SerializedName("topic_count")
    val postCount: Int = 0,

    @SerializedName("create_time")
val dateCreated: LocalDateTime? = null,

    @SerializedName("sharing_url")
    val url: String? = null,

    @SerializedName("avatar")
    val avatarUrl: String? = null,

    @SerializedName("member_name")
    val memberName: String? = null,

    @SerializedName("desc_abstract")
    val shortDescription: String? = null,

    @SerializedName("background_mask_color")
    val colorString: String? = null,
){
    @get:Ignore
    val color: Int
        get() = Color.parseColor(if (TextUtils.isEmpty(colorString)) "#FFFFFF" else colorString)
}

