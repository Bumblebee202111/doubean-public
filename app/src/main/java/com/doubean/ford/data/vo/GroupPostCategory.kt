package com.doubean.ford.data.vo

import androidx.room.Entity

@Entity
class GroupPostCategory {
    val id = 0
    val name: String? = null
    val desc: String? = null
    val rules: List<GroupPostCategoryRule>? = null
}