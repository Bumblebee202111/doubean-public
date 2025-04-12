package com.github.bumblebee202111.doubean.model.subjects

data class SubjectWithInterest<T : Subject>(
    val subject: T,
    override val interest: SubjectInterest = SubjectInterest(
        null,
        SubjectInterestStatus.MARK_STATUS_UNMARK
    ),
) : MarkableSubject {
    override val id: String
        get() = subject.id
    override val type: SubjectType
        get() = subject.type
}
