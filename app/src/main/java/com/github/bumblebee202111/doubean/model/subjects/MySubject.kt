package com.github.bumblebee202111.doubean.model.subjects

data class MySubject(
    val interests: List<MySubjectStatus>,
    val name: String,
    val type: SubjectType,
)

