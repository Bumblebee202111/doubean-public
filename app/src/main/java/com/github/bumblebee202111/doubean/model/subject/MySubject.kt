package com.github.bumblebee202111.doubean.model.subject

data class MySubject(
    val interests: List<MySubjectStatus>,
    val name: String,
    val type: SubjectType,
)