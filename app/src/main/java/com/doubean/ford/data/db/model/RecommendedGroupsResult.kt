package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.doubean.ford.model.GroupRecommendationType

@Entity(tableName = "recommended_groups_results")
class RecommendedGroupsResult(
    @PrimaryKey
    @ColumnInfo("recommendation_type")
    val recommendationType: GroupRecommendationType,
    val ids: List<String>,
)