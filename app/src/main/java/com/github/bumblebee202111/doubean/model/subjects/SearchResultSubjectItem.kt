package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.shared.subject.model.Rating
import com.github.bumblebee202111.doubean.shared.subject.model.SubjectType

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
