package com.github.bumblebee202111.doubean.feature.groups.model

enum class GroupJoinType(private val value: String) {
    ALL("A"),
    INVITE("I"),
    MOBILE("M"),
    NO("N"),
    RATIFY("R");

    companion object {
        fun of(value: String) = entries.first { it.value == value }
    }
}