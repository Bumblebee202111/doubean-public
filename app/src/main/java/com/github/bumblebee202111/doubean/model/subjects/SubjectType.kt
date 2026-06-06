package com.github.bumblebee202111.doubean.model.subjects

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.github.bumblebee202111.doubean.R

@Keep
enum class SubjectType(
    val statuses: List<SubjectInterestStatus>,
    @StringRes val typeNameResId: Int?,
    @StringRes val titleNameResId: Int?,
    @StringRes val unitResId: Int?,
    @StringRes val reviewTitleResId: Int?,
    @StringRes val vendorTitleResId: Int?,
    val statusTextResIds: Map<SubjectInterestStatus, Int>,
    val statusActionTextResIds: Map<SubjectInterestStatus, Int>,
) {
    MOVIE(
        statuses = listOf(
            SubjectInterestStatus.MARK_STATUS_UNMARK,
            SubjectInterestStatus.MARK_STATUS_MARK,
            SubjectInterestStatus.MARK_STATUS_DONE,
        ),
        typeNameResId = R.string.movie,
        titleNameResId = R.string.title_movie,
        unitResId = R.string.unit_for_movie,
        reviewTitleResId = R.string.title_review_movie,
        vendorTitleResId = R.string.movie_vendor_entrance_title,
        statusTextResIds = mapOf(
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_done_movie,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.status_mark_movie
        ),
        statusActionTextResIds = mapOf(
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.action_mark_movie,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_action_done_movie,
            SubjectInterestStatus.MARK_STATUS_UNMARK to R.string.status_action_unmark,
        )
    ),
    TV(
        statuses = listOf(
            SubjectInterestStatus.MARK_STATUS_UNMARK,
            SubjectInterestStatus.MARK_STATUS_MARK,
            SubjectInterestStatus.MARK_STATUS_DOING,
            SubjectInterestStatus.MARK_STATUS_DONE,
        ),
        typeNameResId = R.string.tv,
        titleNameResId = R.string.title_tv,
        unitResId = R.string.unit_for_movie,
        reviewTitleResId = R.string.title_review_tv,
        vendorTitleResId = R.string.tv_vendor_entrance_title,
        statusTextResIds = mapOf(
            SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_doing_tv,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_done_tv,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.status_mark_tv
        ),
        statusActionTextResIds = mapOf(
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.action_mark_tv,
            SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_action_doing_tv,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_action_done_tv,
            SubjectInterestStatus.MARK_STATUS_UNMARK to R.string.status_action_unmark,
        )
    ),
    BOOK(
        statuses = listOf(
            SubjectInterestStatus.MARK_STATUS_UNMARK,
            SubjectInterestStatus.MARK_STATUS_MARK,
            SubjectInterestStatus.MARK_STATUS_DOING,
            SubjectInterestStatus.MARK_STATUS_DONE,
        ),
        typeNameResId = R.string.book,
        titleNameResId = R.string.title_book,
        unitResId = R.string.unit_for_book,
        reviewTitleResId = R.string.title_review_book,
        vendorTitleResId = null,
        statusTextResIds = mapOf(
            SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_doing_book,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_done_book,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.status_mark_book
        ),
        statusActionTextResIds = mapOf(
            SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_action_doing_book,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_action_done_book,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.action_mark_book,
            SubjectInterestStatus.MARK_STATUS_UNMARK to R.string.status_action_unmark
        )
    ),
    MUSIC(
        statuses = listOf(
            SubjectInterestStatus.MARK_STATUS_DOING,
            SubjectInterestStatus.MARK_STATUS_DONE,
            SubjectInterestStatus.MARK_STATUS_MARK,
            SubjectInterestStatus.MARK_STATUS_UNMARK
        ),
        typeNameResId = R.string.title_music,
        titleNameResId = R.string.title_music,
        unitResId = R.string.unit_for_music,
        reviewTitleResId = R.string.title_review_music,
        vendorTitleResId = null,
        statusTextResIds = mapOf(
            SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_doing_music,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_done_music,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.status_mark_music
        ),
        statusActionTextResIds = mapOf(
            SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_action_doing_music,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_action_done_music,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.action_mark_music,
            SubjectInterestStatus.MARK_STATUS_UNMARK to R.string.status_action_unmark
        )
    ),
    UNSUPPORTED(
        statuses = emptyList(),
        typeNameResId = null,
        titleNameResId = null,
        unitResId = R.string.unit_for_generic_subject,
        reviewTitleResId = null,
        vendorTitleResId = null,
        statusTextResIds = emptyMap(),
        statusActionTextResIds = emptyMap()
    );

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