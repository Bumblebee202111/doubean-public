package com.github.bumblebee202111.doubean.model.subjects

import androidx.annotation.Keep

data class MySubject(
    val interests: List<MySubjectStatus>,
    val name: String,
    val type: SubjectType,
)

@Keep
enum class SubjectType(val statuses: List<SubjectInterestStatus>) {
    MOVIE(
        listOf(
            SubjectInterestStatus.MARK_STATUS_UNMARK,
            SubjectInterestStatus.MARK_STATUS_MARK,
            SubjectInterestStatus.MARK_STATUS_DONE,

            )
    ),
    TV(
        listOf(
            SubjectInterestStatus.MARK_STATUS_UNMARK,
            SubjectInterestStatus.MARK_STATUS_MARK,
            SubjectInterestStatus.MARK_STATUS_DOING,
            SubjectInterestStatus.MARK_STATUS_DONE,

            )
    ),
    BOOK
        (
        listOf(
            SubjectInterestStatus.MARK_STATUS_UNMARK,
            SubjectInterestStatus.MARK_STATUS_MARK,
            SubjectInterestStatus.MARK_STATUS_DOING,
            SubjectInterestStatus.MARK_STATUS_DONE,
        )
    ),
    UNSUPPORTED(emptyList())
}
