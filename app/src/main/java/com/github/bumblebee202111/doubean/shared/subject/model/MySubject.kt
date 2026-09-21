package com.github.bumblebee202111.doubean.shared.subject.model

data class MySubject(
    val interests: List<MySubjectStatus>,
    val name: String,
    val type: SubjectType,
)