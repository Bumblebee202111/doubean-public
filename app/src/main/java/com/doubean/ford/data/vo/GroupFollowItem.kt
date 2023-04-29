package com.doubean.ford.data.vo

data class GroupFollowItem(val groupId: String, val groupTabId: String?, val group: GroupDetail?) {
    val groupMemberName: String?
    val groupMemberCount: Int?
    val groupName: String?
    val groupAvatarUrl: String?
    val groupTabName: String?
    val groupColor: String?

    init {
        if(group!=null) {
            groupName = group.name
            groupAvatarUrl = group.avatarUrl
            groupTabName = if (groupTabId != null) findGroupTabName(group) else null
            groupColor = group.colorString
            groupMemberCount = group.memberCount
            groupMemberName = group.memberName
        }
        else{
            groupName = null
            groupAvatarUrl = null
            groupTabName = null
            groupColor = null
            groupMemberCount = null
            groupMemberName = null
        }
    }

    private fun findGroupTabName(group: GroupDetail): String? {
        for (tab in group.tabs!!) {
            if (tab.id == groupTabId) {
                return tab.name
            }
        }
        return null
    }
}