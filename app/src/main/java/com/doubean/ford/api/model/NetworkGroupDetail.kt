package com.doubean.ford.api.model

import com.doubean.ford.data.db.model.GroupDetailPartialEntity
import com.doubean.ford.data.db.model.GroupTabEntity
import com.doubean.ford.util.parseHexColor
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDateTime

data class NetworkGroupDetail(
    val id: String,

    val name: String,

    @SerializedName("member_count")
    val memberCount: Int,

    @SerializedName("topic_count")
    val postCount: Int,

    @SerializedName("create_time")
    val dateCreated: LocalDateTime,

    @SerializedName("sharing_url")
    val shareUrl: String,

    val url: String,

    @SerializedName("avatar")
    val avatarUrl: String,

    @SerializedName("member_name")
    val memberName: String,

    @SerializedName("desc")
    val description: String,

    @SerializedName("topic_tags_normal")
    val postTags: List<NetworkGroupPostTag>,

    @SerializedName("group_tabs")
    val tabs: List<NetworkGroupTab>,

    @SerializedName("background_mask_color")
    val colorString: String,

    val uri: String,

    val owner: NetworkUser,
)

data class NetworkGroupTab(
    val id: String,
    val name: String?,
    val seq: Int,
) : Serializable

fun NetworkGroupDetail.asPartialEntity() = GroupDetailPartialEntity(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    memberName = memberName,
    memberCount = memberCount,
    postCount = postCount,
    dateCreated = dateCreated,
    description = description,
    shareUrl = shareUrl,
    url = url,
    uri = uri,
    color = parseHexColor(colorString)
)


fun NetworkGroupTab.asEntity(groupId: String) = GroupTabEntity(
    id = id,
    name = name,
    seq = seq,
    groupId = groupId
)