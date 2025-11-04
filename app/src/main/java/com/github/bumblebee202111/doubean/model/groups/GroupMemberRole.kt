package com.github.bumblebee202111.doubean.model.groups

enum class GroupMemberRole(private val value: Int) {
    MEMBER(1001),
    MEMBER_ADMIN(1002),
    MEMBER_BANNED(1004),
    MEMBER_INVITED(1003),
    MEMBER_INVITED_WAIT_FOR_ADMIN(1006),
    MEMBER_REQUESTED_WAIT_FOR_ADMIN(1005),
    NOT_MEMBER(1000);

    companion object {
        fun of(value: Int) = GroupMemberRole.entries.first { it.value == value }
    }
}