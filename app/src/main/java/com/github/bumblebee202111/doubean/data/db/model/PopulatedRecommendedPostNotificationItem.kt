package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.RecommendedPostNotificationItem

class PopulatedRecommendedPostNotificationItem(
    @Embedded
    val entity: RecommendedPostNotificationEntity,

    @Relation(
        parentColumn = "post_id",
        entityColumn = "id",
        entity = PostEntity::class
    )
    val postWithGroup: PopulatedPostItemWithGroup,
)

fun PopulatedRecommendedPostNotificationItem.asExternalModel(): RecommendedPostNotificationItem {
    val postItemWithGroup = postWithGroup.asExternalModel()
    return RecommendedPostNotificationItem(
        postItemWithGroup.id,
        postItemWithGroup.title,
        postItemWithGroup.author,
        postItemWithGroup.created,
        postItemWithGroup.lastUpdated,
        postItemWithGroup.commentCount,
        postItemWithGroup.tags,
        postItemWithGroup.coverUrl,
        postItemWithGroup.url,
        postItemWithGroup.uri,
        postItemWithGroup.group
    )
}
