package com.github.bumblebee202111.doubean.model.subjects

data class RecommendSubject(
    val id: String,
    val title: String,
    val imageUrl: String,
    val rating: Rating,
    val type: SubjectType,
    val uri: String,
    val cardSubtitle: String,
    val interest: SubjectInterest?,
)