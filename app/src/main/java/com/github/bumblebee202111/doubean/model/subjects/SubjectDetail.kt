package com.github.bumblebee202111.doubean.model.subjects

sealed interface SubjectDetail : MarkableSubject {
    override val id: String
    val rating: Rating
    val title: String
    val cardSubtitle: String
    val coverUrl: String
    val uri: String
    val intro: String
    override val type: SubjectType
    override val interest: SubjectInterest?
}