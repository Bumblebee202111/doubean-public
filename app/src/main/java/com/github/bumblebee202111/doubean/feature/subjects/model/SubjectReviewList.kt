package com.github.bumblebee202111.doubean.feature.subjects.model

data class SubjectReviewList(
    val count: Int,
    val start: Int,
    val total: Int,
    val reviews: List<SubjectReview>,
)
