package com.github.bumblebee202111.doubean.model.subjects

data class Music(
    override val id: String,
    override val rating: Rating,
    override val cardSubtitle: String,
    override val title: String,
    override val imageUrl: String,
    override val uri: String,
    val singer: List<String>,
) : Subject {
    override val type: SubjectType = SubjectType.MUSIC
}