package com.github.bumblebee202111.doubean.model.status

import com.github.bumblebee202111.doubean.model.subject.Subject
import com.github.bumblebee202111.doubean.model.subject.SubjectInterestStatus

data class SubjectStatusCard<T : Subject>(
    val subject: T,
    val status: SubjectInterestStatus? = null,
) : StatusCardData
