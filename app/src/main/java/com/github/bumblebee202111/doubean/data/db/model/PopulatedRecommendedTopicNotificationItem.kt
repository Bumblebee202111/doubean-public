package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.bumblebee202111.doubean.model.TopicItemWithGroup

data class PopulatedRecommendedTopicNotificationItem(
    @Embedded
    val entity: RecommendedTopicNotificationEntity,

    @Relation(
        parentColumn = "topic_id",
        entityColumn = "id",
        entity = TopicEntity::class
    )
    val topicWithGroup: PopulatedTopicItemWithGroup,
)

fun PopulatedRecommendedTopicNotificationItem.asExternalModel(): TopicItemWithGroup {
    return topicWithGroup.asExternalModel()

}
