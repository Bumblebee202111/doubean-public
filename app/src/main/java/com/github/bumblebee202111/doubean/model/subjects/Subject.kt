package com.github.bumblebee202111.doubean.model.subjects

sealed interface Subject {
    val id: String
    val rating: Rating
    val title: String
    val cardSubtitle: String
    val imageUrl: String
    val uri: String
    val type: SubjectType

    data class Unsupported(
        override val id: String,
        override val rating: Rating,
        override val title: String,
        override val cardSubtitle: String,
        override val imageUrl: String,
        override val uri: String,
    ) : Subject {
        override val type: SubjectType = SubjectType.UNSUPPORTED
    }
}
