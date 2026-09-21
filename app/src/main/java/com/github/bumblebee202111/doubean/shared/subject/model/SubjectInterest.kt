package com.github.bumblebee202111.doubean.shared.subject.model

data class SubjectInterest(
    val comment: String?,
    val rating: Rating?,
    val status: SubjectInterestStatus,
)