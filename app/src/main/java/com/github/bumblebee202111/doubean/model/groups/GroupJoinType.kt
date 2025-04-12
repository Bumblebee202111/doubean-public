package com.github.bumblebee202111.doubean.model.groups

enum class GroupJoinType(private val value: String) {
    ALL("A"),
    INVITE("I"),
    MOBILE("M"),
    NO("N"),
    RATIFY("R");

    companion object {
        fun of(value: String) = GroupJoinType.entries.first { it.value == value }
    }
}