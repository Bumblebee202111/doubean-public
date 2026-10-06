package com.github.bumblebee202111.doubean.model.subject

data class SubjectInterestWithUserList(
    val interests: List<SubjectInterestWithUser>,
    val total: Int,
)