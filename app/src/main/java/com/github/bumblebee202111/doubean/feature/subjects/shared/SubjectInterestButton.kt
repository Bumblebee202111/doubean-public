package com.github.bumblebee202111.doubean.feature.subjects.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.MarkableSubject
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectType

@Composable
fun SubjectInterestButtons(
    subject: MarkableSubject,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val interestStatuses =
                    subject.type.statuses.filter { it != SubjectInterestStatus.MARK_STATUS_UNMARK }
                interestStatuses.forEachIndexed { index, status ->
                    SegmentedButton(
                        selected = status == subject.interest?.status,
                        onClick = {
                            onUpdateStatus(status)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = interestStatuses.size,
                        ),
                        enabled = status != subject.interest?.status,
                        icon = {
                            val icon =
                                if (subject.interest?.status == status) SubjectCurrentStatusIconVector
                                else SubjectStatusActionIconsMap.getValue(status).asVector()
                            Icon(
                                imageVector = icon,
                                contentDescription = null
                            )


                        }
                    ) {
                        val textResId = if (subject.interest?.status == status) {
                            SubjectStatusTextResIdsMap.getValue(subject.type)
                                .getValue(status)
                        } else {
                            SubjectStatusActionTextResIdsMap.getValue(subject.type)
                                .getValue(status)
                        }
                        Text(text = stringResource(id = textResId), overflow = TextOverflow.Clip)
                    }
                }
            }
        }
        if (subject.interest != null && subject.interest?.status != SubjectInterestStatus.MARK_STATUS_UNMARK) {
            SubjectInterestUnmarkButton(subject = subject, onUpdateStatus = onUpdateStatus)
        }
    }
}

@Composable
fun SubjectInterestUnmarkButton(
    subject: MarkableSubject,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
) {
    TextButton(onClick = {
        onUpdateStatus(SubjectInterestStatus.MARK_STATUS_UNMARK)
    }) {
        SubjectSingleInterestButtonContent(
            iconVector = SubjectStatusActionIconsMap.getValue(SubjectInterestStatus.MARK_STATUS_UNMARK)
                .asVector(),
            textResId = SubjectStatusActionTextResIdsMap.getValue(subject.type)
                .getValue(SubjectInterestStatus.MARK_STATUS_UNMARK)
        )
    }
}

@Composable
fun SubjectSingleInterestButtonContent(iconVector: ImageVector, textResId: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = iconVector,
            contentDescription = null
        )

        Text(
            text = stringResource(id = textResId),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

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