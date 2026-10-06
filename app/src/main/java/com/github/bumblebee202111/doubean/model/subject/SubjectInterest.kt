package com.github.bumblebee202111.doubean.model.subject

data class SubjectInterest(
    val comment: String?,
    val rating: Rating?,
    val status: SubjectInterestStatus,
)