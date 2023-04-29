package com.doubean.ford.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommended_groups_result")
class RecommendedGroupsResult(
    @PrimaryKey val type: GroupRecommendationType,
    val ids: List<Long>
)