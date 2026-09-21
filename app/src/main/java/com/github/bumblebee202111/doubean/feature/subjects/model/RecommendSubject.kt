package com.github.bumblebee202111.doubean.feature.subjects.model

import com.github.bumblebee202111.doubean.shared.subject.model.Rating
import com.github.bumblebee202111.doubean.shared.subject.model.SubjectInterest
import com.github.bumblebee202111.doubean.shared.subject.model.SubjectType

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