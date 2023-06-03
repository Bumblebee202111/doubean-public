package com.doubean.ford.model

import androidx.room.Entity

/** Allows the user to browse posts by custom categories
 * The structure is like: ("美食", ((中餐|安利), (西餐|全部)))
 * The feature is only imagined and won't be implemented shortly or will never be implemented
 * */
@Entity
class GroupPostCategory {
    val id = 0
    val name: String? = null
    val desc: String? = null
    val rules: List<GroupPostCategoryRule>? = null
}

class GroupPostCategoryRegex {
    val id = 0
    val words: List<String>? = null
}

@Entity
class GroupPostCategoryRule {
    var id = 0
    var groupIds: List<Int>? = null
    var groupPostTagId: List<Int>? = null
    var regex: String? = null
    var notRegex: String? = null
    var strict = false
}