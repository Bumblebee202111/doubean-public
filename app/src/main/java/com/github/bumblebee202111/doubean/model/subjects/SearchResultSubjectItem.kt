package com.github.bumblebee202111.doubean.model.subjects

data class SearchResultSubjectItem(
    val hasLinewatch: Boolean?,
    val controversyReason: String,
    val title: String,
    val abstract: String?,
    val uri: String,
    val coverUrl: String,
    val year: String?,
    val cardSubtitle: String,
    val id: String,
    val nullRatingReason: String,
    val rating: Rating,
    val type: SubjectType,
)
