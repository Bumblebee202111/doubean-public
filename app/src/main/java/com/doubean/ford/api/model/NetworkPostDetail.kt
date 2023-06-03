package com.doubean.ford.api.model

import com.doubean.ford.data.db.model.PostDetailPartialEntity
import com.doubean.ford.data.db.model.PostGroupPartialEntity
import com.doubean.ford.data.db.model.PostTagCrossRef
import com.doubean.ford.util.ColorUtils
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime


data class NetworkPostDetail(
    val id: String,

    val title: String,

    val author: NetworkUser,

    @SerializedName("create_time")
    val created: LocalDateTime,

    @SerializedName("update_time")
    val lastUpdated: LocalDateTime,

    @SerializedName("like_count")
    val likeCount: Int,

    @SerializedName("reactions_count")
    val reactionCount: Int,

    @SerializedName("reshares_count")
    val repostCount: Int,

    @SerializedName("collections_count")
    val saveCount: Int,

    @SerializedName("comments_count")
    val commentCount: Int,

    @SerializedName("abstract")
    val shortContent: String,

    val content: String,

    @SerializedName("topic_tags")
    val postTags: List<NetworkGroupPostTag>,

    @SerializedName("cover_url")
    val coverUrl: String,

    val url: String,

    val group: NetworkPostGroup,

    val uri: String,

    )

data class NetworkPostGroup(
    val id: String,

    val name: String,

    @SerializedName("member_count")
    val memberCount: Int,

    @SerializedName("topic_count")
    val postCount: Int,

    @SerializedName("create_time")
    val dateCreated: LocalDateTime,

    @SerializedName("sharing_url")
    val url: String,

    @SerializedName("avatar")
    val avatarUrl: String,

    @SerializedName("member_name")
    val memberName: String,

    @SerializedName("desc_abstract")
    val shortDescription: String? = null,

    /*Format: "#123abc"*/
    @SerializedName("background_mask_color")
    val colorString: String,
)

fun NetworkPostDetail.asPartialEntity() = PostDetailPartialEntity(
    id = id,
    title = title,
    authorId = author.id,
    created = created,
    lastUpdated = lastUpdated,
    likeCount = likeCount,
    reactionCount = reactionCount,
    repostCount = repostCount,
    saveCount = saveCount,
    commentCount = commentCount,
    shortContent = shortContent,
    content = content,
    coverUrl = coverUrl,
    url = url,
    uri = uri,
    groupId = group.id)


fun NetworkPostDetail.tagCrossRefs(): List<PostTagCrossRef> =
    postTags.map { tag ->
        PostTagCrossRef(id, tag.id)
    }

fun NetworkPostGroup.asPartialEntity() = PostGroupPartialEntity(
    id = id,
    name = name,
    memberCount = memberCount,
    postCount = postCount,
    dateCreated = dateCreated,
    url = url,
    avatarUrl = avatarUrl,
    memberName = memberName,
    shortDescription = shortDescription,
    color = ColorUtils.parseHexColor(colorString)
)
