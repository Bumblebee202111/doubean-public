package com.doubean.ford.data.vo

import android.graphics.Color
import android.text.TextUtils
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime


data class GroupDetail(
    val id: String,
    val name: String,

    @SerializedName("member_count")
    val memberCount: Int = 0,

    @SerializedName("topic_count")
    val postCount: Int = 0,

    @SerializedName("create_time")
    val dateCreated: LocalDateTime? = null,

    @JvmField
    @SerializedName("sharing_url")
    val url: String,

    @SerializedName("avatar")
    val avatarUrl: String? = null,

    @SerializedName("member_name")
    val memberName: String? = null,

    @SerializedName("desc")
    val description: String? = null,

    @SerializedName("group_tabs")
    val tabs: List<GroupTab>? = null,

    @SerializedName("background_mask_color")
    val colorString: String? = null,

    val uri: String
) : java.io.Serializable {

    val color: Int
        get() = Color.parseColor(if (TextUtils.isEmpty(colorString)) "#FFFFFF" else colorString)

    @Ignore
    fun toGroupItem() = GroupItem(
        name = name,
        url = url,
        uri = uri,
        dateCreated = dateCreated,
        avatarUrl = avatarUrl,
        shortDescription = description,
        id = id,
        postCount = postCount
    )
}

    
