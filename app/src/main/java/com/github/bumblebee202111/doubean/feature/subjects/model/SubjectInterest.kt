package com.github.bumblebee202111.doubean.feature.subjects.model

import com.github.bumblebee202111.doubean.model.subjects.Rating
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus

data class SubjectInterest(
    val comment: String?,
    val rating: Rating?,
    val status: SubjectInterestStatus,
)