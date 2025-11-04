package com.github.bumblebee202111.doubean.model.subjects

data class SubjectInterestWithUserList(
    val interests: List<SubjectInterestWithUser>,
    val total: Int,
)