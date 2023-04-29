package com.doubean.ford.data.vo

import androidx.room.Entity

@Entity
class GroupPostCategoryRule {
    var id = 0
    var groupIds: List<Int>? = null
    var groupPostTagId: List<Int>? = null
    var regex: String? = null
    var notRegex: String? = null
    var strict = false
}