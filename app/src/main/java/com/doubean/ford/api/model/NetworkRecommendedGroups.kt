package com.doubean.ford.api.model

import com.doubean.ford.data.db.model.RecommendedGroupEntity
import com.doubean.ford.data.db.model.RecommendedGroupItemGroupPartialEntity
import com.doubean.ford.data.db.model.RecommendedGroupPost
import com.doubean.ford.model.GroupRecommendationType
import com.doubean.ford.util.parseHexColor
import com.google.gson.annotations.SerializedName

/**
 * Data class that represents a recommendation of groups response from Douban.
 */
class NetworkRecommendedGroups(
    @SerializedName("groups")
    val items: List<NetworkRecommendedGroup>,
    val title: String,
    val count: Int,
)

class NetworkRecommendedGroup(
    // A "new" string or an integer representing the change in the ranking of the group
    val trend: String,

    @SerializedName("topics")
    val posts: List<NetworkPostItem>,

    val group: NetworkRecommendedGroupItemGroup,
)

class NetworkRecommendedGroupItemGroup(
    val id: String,
    val name: String,

    @SerializedName("member_count")
    val memberCount: Int,

    @SerializedName("topic_count")
    val postCount: Int,

    @SerializedName("sharing_url")
    val shareUrl: String,

    val url: String,

    val uri: String,

    @SerializedName("avatar")
    val avatarUrl: String,

    @SerializedName("member_name")
    val memberName: String,

    @SerializedName("desc_abstract")
    val shortDescription: String?,

    @SerializedName("background_mask_color")
    val colorString: String,
)

fun NetworkRecommendedGroupItemGroup.asPartialEntity() = RecommendedGroupItemGroupPartialEntity(
    id = id,
    name = name,
    memberCount = memberCount,
    postCount = postCount,
    shareUrl = shareUrl,
    url = url,
    uri = uri,
    avatarUrl = avatarUrl,
    memberName = memberName,
    shortDescription = shortDescription,
    color = parseHexColor(colorString)
)

fun NetworkRecommendedGroup.asEntity(groupRecommendationType: GroupRecommendationType, no: Int) =
    RecommendedGroupEntity(
        recommendationType = groupRecommendationType,
        no = no,
        groupId = group.id
    )

fun NetworkRecommendedGroup.postRefs(): List<RecommendedGroupPost> =
    posts.map { RecommendedGroupPost(group.id, it.id) }
