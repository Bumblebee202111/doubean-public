package com.github.bumblebee202111.doubean.model.groups

data class Question(
    val correctAnswer: String,
    val expireTime: String?,
    val id: String,
    val inputType: String,
    val ownerId: String,
    val postedAnswer: String,
    val postedUserCount: Int,
    val title: String,
    val userStat: UserStat,
) : TopicContentEntity {
    data class UserStat(
        val correct: Item,
        val wrong: Item,
    ) {
        data class Item(
            val userTotal: Int,
        )
    }
}