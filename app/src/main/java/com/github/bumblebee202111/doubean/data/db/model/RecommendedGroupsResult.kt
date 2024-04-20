package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.GroupRecommendationType

@Entity(tableName = "recommended_groups_results")
class RecommendedGroupsResult(
    @PrimaryKey
    @ColumnInfo("recommendation_type")
    val recommendationType: GroupRecommendationType,
    val ids: List<String>,
)