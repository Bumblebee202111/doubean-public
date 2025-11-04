package com.github.bumblebee202111.doubean.model.subjects

import androidx.annotation.Keep

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
    UNSUPPORTED(emptyList());

    companion object {
        fun fromString(type: String): SubjectType {
            return when (type) {
                "movie" -> MOVIE
                "tv" -> TV
                "book" -> BOOK
                else -> UNSUPPORTED
            }
        }
    }
}