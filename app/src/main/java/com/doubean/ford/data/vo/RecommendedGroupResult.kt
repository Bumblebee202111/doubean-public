package com.doubean.ford.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommended_groups_results")
class RecommendedGroupResult(
    val no: Int, val groupId: String, val postId: String?, @PrimaryKey(autoGenerate = true)
    val id: Long
)