package com.github.bumblebee202111.doubean.model.subject

data class SubjectWithRank<T : Subject>(
    val subject: T,
    val rankValue: Int,
)
