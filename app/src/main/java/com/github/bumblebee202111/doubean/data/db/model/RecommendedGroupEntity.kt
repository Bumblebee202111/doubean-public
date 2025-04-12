package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.github.bumblebee202111.doubean.model.groups.GroupRecommendationType

@Entity(tableName = "recommended_groups", primaryKeys = ["recommendation_type", "no"])
data class RecommendedGroupEntity(
    @ColumnInfo("recommendation_type")
    val recommendationType: GroupRecommendationType,
    val no: Int,
    @ColumnInfo("group_id")
    val groupId: String,
    //@ColumnInfo("topic_ids")
    //val topicIds: List<String>
)