package com.github.bumblebee202111.doubean.ui.common.subject

import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectSingleInterestButtonContent
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterest
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

/**
 * Mark or marked
 */
@Composable
fun SubjectSimpleInterestButton(
    subjectType: SubjectType,
    interest: SubjectInterest,
    modifier: Modifier = Modifier,
    onMarkClick: () -> Unit,
) {
    when (val status = interest.status) {
        SubjectInterestStatus.MARK_STATUS_UNMARK -> {
            FilledTonalButton(onClick = onMarkClick, modifier = modifier) {
                SubjectSingleInterestButtonContent(
                    iconVector = SubjectStatusActionIconsMap.getValue(
                        SubjectInterestStatus.MARK_STATUS_MARK
                    ).asVector(),
                    textResId = SubjectStatusActionTextResIdsMap.getValue(subjectType)
                        .getValue(SubjectInterestStatus.MARK_STATUS_MARK)
                )
            }
        }

        else -> {
            FilledTonalButton(onClick = { }, modifier = modifier, enabled = false) {
                SubjectSingleInterestButtonContent(
                    iconVector = SubjectCurrentStatusIconVector,
                    textResId = SubjectStatusTextResIdsMap.getValue(subjectType)
                        .getValue(status)
                )
            }
        }
    }

}