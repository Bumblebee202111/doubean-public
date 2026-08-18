package com.github.bumblebee202111.doubean.ui.common.subject

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