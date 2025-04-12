package com.github.bumblebee202111.doubean.model.subjects

data class Movie(
    override val id: String,
    override val rating: Rating,
    override val cardSubtitle: String,
    override val title: String,
    override val imageUrl: String,
    override val uri: String,
) : Subject {
    override val type: SubjectType = SubjectType.MOVIE
}