package com.doubean.ford.api.model

import com.doubean.ford.data.db.model.GroupSearchResultGroupItemPartialEntity
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Data class that represents a group search response from Douban.
 */

class NetworkGroupSearch(
    override val start: Int,
    override val count: Int,
    override val total: Int,
    private val q: String,
    @SerializedName(value = "items")
    override val items: List<NetworkGroupSearchResultItem>,
) : NetworkPagedList<NetworkGroupSearchResultItem>

class NetworkGroupSearchResultItem(
    @SerializedName("target")
    val group: NetworkGroupSearchResultGroupItem,
)

class NetworkGroupSearchResultGroupItem(
    val id: String,

    val name: String,

    @SerializedName("member_count")
    val memberCount: Int,

    @SerializedName("topic_count")
    val postCount: Int,

    @SerializedName("create_time")
    val dateCreated: LocalDateTime? = null,

    @SerializedName("sharing_url")
    val shareUrl: String,

    val url: String,

    val uri: String,

    @SerializedName("avatar")
    val avatarUrl: String? = null,

    @SerializedName("member_name")
    val memberName: String? = null,

    @SerializedName("desc_abstract")
    val shortDescription: String? = null,
)

fun NetworkGroupSearchResultGroupItem.asPartialEntity() = GroupSearchResultGroupItemPartialEntity(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    memberName = memberName,
    memberCount = memberCount,
    postCount = postCount,
    dateCreated = dateCreated,
    shortDescription = shortDescription,
    shareUrl = shareUrl,
    url = url,
    uri = uri
)