package com.github.bumblebee202111.doubean.feature.subjects.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

val SubjectStatusActionTextResIdsMap = mapOf(
    SubjectType.MOVIE to mapOf(
        SubjectInterestStatus.MARK_STATUS_MARK to R.string.action_mark_movie,
        SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_action_done_movie,
        SubjectInterestStatus.MARK_STATUS_UNMARK to R.string.status_action_unmark,
    ),

    SubjectType.TV to mapOf(
        SubjectInterestStatus.MARK_STATUS_MARK to R.string.action_mark_tv,
        SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_action_doing_tv,
        SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_action_done_tv,
        SubjectInterestStatus.MARK_STATUS_UNMARK to R.string.status_action_unmark,
    ),
    SubjectType.BOOK to mapOf(
        SubjectInterestStatus.MARK_STATUS_MARK to R.string.action_mark_book,
        SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_action_doing_book,
        SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_action_done_book,
        SubjectInterestStatus.MARK_STATUS_UNMARK to R.string.status_action_unmark
    ),
)

val SubjectStatusTextResIdsMap =
    mapOf(
        SubjectType.MOVIE to mapOf(
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_done_movie,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.status_mark_movie
        ),
        SubjectType.TV to mapOf(
            SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_doing_tv,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_done_tv,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.status_mark_tv
        ),
        SubjectType.BOOK to mapOf(
            SubjectInterestStatus.MARK_STATUS_DOING to R.string.status_doing_book,
            SubjectInterestStatus.MARK_STATUS_DONE to R.string.status_done_book,
            SubjectInterestStatus.MARK_STATUS_MARK to R.string.status_mark_book
        ),
    )

val SubjectStatusActionIconsMap =
    mapOf(
        SubjectInterestStatus.MARK_STATUS_MARK to SubjectStatusActionIcon.Vector(Icons.Default.Add),
        SubjectInterestStatus.MARK_STATUS_DOING to SubjectStatusActionIcon.DrawableRes(R.drawable.ic_progress_activity),
        SubjectInterestStatus.MARK_STATUS_DONE to SubjectStatusActionIcon.Vector(Icons.Default.Done),
        SubjectInterestStatus.MARK_STATUS_UNMARK to SubjectStatusActionIcon.Vector(Icons.Default.Remove)
    )

val SubjectCurrentStatusIconVector = Icons.Default.CheckCircleOutline

sealed interface SubjectStatusActionIcon {
    class Vector(val vector: ImageVector) : SubjectStatusActionIcon {
        @Composable
        override fun asVector() = vector
    }

    class DrawableRes(@androidx.annotation.DrawableRes val id: Int) : SubjectStatusActionIcon {
        @Composable
        override fun asVector(): ImageVector {
            return ImageVector.vectorResource(id = id)
        }
    }

    @Composable
    fun asVector(): ImageVector
}