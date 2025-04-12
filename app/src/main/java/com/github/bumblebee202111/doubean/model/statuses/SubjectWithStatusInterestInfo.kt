package com.github.bumblebee202111.doubean.model.statuses

import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus

data class SubjectWithStatusInterestInfo<T : Subject>(
    val subject: T,
    val status: SubjectInterestStatus? = null,
) : StatusCardData
