package com.github.bumblebee202111.doubean.model.subjects

data class SubjectWithRankAndInterest<T : Subject>(
    val subject: T,
    val rankValue: Int,
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
