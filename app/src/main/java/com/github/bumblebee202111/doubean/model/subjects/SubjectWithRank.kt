package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.shared.subject.model.Subject

data class SubjectWithRank<T : Subject>(
    val subject: T,
    val rankValue: Int,
)
