package com.doubean.ford.api.model

import com.doubean.ford.data.db.model.PostItemPartialEntity
import com.doubean.ford.data.db.model.PostTagCrossRef
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class NetworkPostItem(
    val id: String,
    val title: String,
    val author: NetworkUser,

    @SerializedName("create_time")
    val created: LocalDateTime,

    @SerializedName("update_time")
    val lastUpdated: LocalDateTime,

    @SerializedName("comments_count")
    val commentCount: Int,

    @SerializedName("topic_tags")
    val postTags: List<NetworkGroupPostTag>,

    @SerializedName("cover_url")
    val coverUrl: String? = null,
    val url: String,
    val uri: String,
)

fun NetworkPostItem.asPartialEntity(groupId: String) = PostItemPartialEntity(
    id = id,
    title = title,
    authorId = author.id,
    created = created,
    lastUpdated = lastUpdated,
    commentCount = commentCount,
    coverUrl = coverUrl,
    uri = uri,
    url = url,
    groupId = groupId
)

fun NetworkPostItem.tagCrossRefs(): List<PostTagCrossRef> =
    postTags.map { tag ->
        PostTagCrossRef(id, tag.id)
    }