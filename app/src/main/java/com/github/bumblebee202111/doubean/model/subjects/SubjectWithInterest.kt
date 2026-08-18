package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectInterest

data class SubjectWithInterest<T : Subject>(
    val subject: T,
    override val interest: SubjectInterest = SubjectInterest(
        comment = null,
        rating = null,
        status = SubjectInterestStatus.MARK_STATUS_UNMARK
    ),
) : MarkableSubject {
    override val id: String
        get() = subject.id
    override val type: SubjectType
        get() = subject.type
}
