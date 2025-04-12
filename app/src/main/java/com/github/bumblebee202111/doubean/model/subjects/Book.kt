package com.github.bumblebee202111.doubean.model.subjects

data class Book(
    override val id: String,
    override val rating: Rating,
    override val title: String,
    val subtitle: String?,
    override val cardSubtitle: String,
    override val imageUrl: String,
    override val uri: String,
) : Subject {
    override val type: SubjectType = SubjectType.BOOK
}