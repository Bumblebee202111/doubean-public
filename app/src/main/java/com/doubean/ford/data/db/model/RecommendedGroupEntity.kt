package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.doubean.ford.model.GroupRecommendationType

@Entity(tableName = "recommended_groups", primaryKeys = ["recommendation_type", "no"])
class RecommendedGroupEntity(
    @ColumnInfo("recommendation_type")
    val recommendationType: GroupRecommendationType,
    val no: Int,
    @ColumnInfo("group_id")
    val groupId: String,
    //@ColumnInfo("post_ids")
    //val postIds: List<String>
)