package com.github.bumblebee202111.doubean.model.subjects

data class SubjectWithRank<T : Subject>(
    val subject: T,
    val rankValue: Int,
)
