package com.github.bumblebee202111.doubean.ui.common.subject

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectSingleInterestButtonContent
import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.component.DoubeanTonalButton


@Composable
fun SubjectSimpleInterestButton(
    subjectType: SubjectType,
    interest: SubjectInterest,
    modifier: Modifier = Modifier,
    onMarkClick: () -> Unit,
) {
    when (val status = interest.status) {
        SubjectInterestStatus.MARK_STATUS_UNMARK -> {
            DoubeanTonalButton(onClick = onMarkClick, modifier = modifier) {
                SubjectSingleInterestButtonContent(
                    iconVector = SubjectStatusActionIconsMap.getValue(
                        SubjectInterestStatus.MARK_STATUS_MARK
                    ).asVector(),
                    textResId = subjectType.statusActionTextResIds[SubjectInterestStatus.MARK_STATUS_MARK]
                )
            }
        }

        else -> {
            DoubeanTonalButton(onClick = { }, modifier = modifier, enabled = false) {
                SubjectSingleInterestButtonContent(
                    iconVector = SubjectCurrentStatusIconVector,
                    textResId = subjectType.statusTextResIds.getValue(status)
                )
            }
        }
    }
}