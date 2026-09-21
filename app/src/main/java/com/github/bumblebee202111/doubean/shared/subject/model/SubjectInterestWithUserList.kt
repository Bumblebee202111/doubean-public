package com.github.bumblebee202111.doubean.shared.subject.model

data class SubjectInterestWithUserList(
    val interests: List<SubjectInterestWithUser>,
    val total: Int,
)