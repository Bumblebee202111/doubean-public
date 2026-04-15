package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject
import com.github.bumblebee202111.doubean.model.subjects.Rating
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectCurrentStatusIconVector
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectStatusActionIconsMap
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectStatusActionTextResIdsMap
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectStatusTextResIdsMap
import com.github.bumblebee202111.doubean.ui.component.DoubeanTextButton
import com.github.bumblebee202111.doubean.ui.component.doubeanItemShape
import kotlin.math.roundToInt

@Composable
fun SubjectInterestButtons(
    subject: MarkableSubject,
    onUpdateStatus: (newStatus: SubjectInterestStatus, rating: Int?) -> Unit,
) {
    var pendingStatus by remember { mutableStateOf<SubjectInterestStatus?>(null) }

    if (pendingStatus != null) {
        
        val currentRating = subject.interest?.rating?.let {
            if (it is Rating.NonNull) (it.value / it.max * 5).roundToInt() else 0
        } ?: 0

        SubjectRatingDialog(
            initialRating = currentRating,
            onDismissRequest = { pendingStatus = null },
            onConfirm = { rating ->
                onUpdateStatus(pendingStatus!!, rating)
                pendingStatus = null
            }
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val interestStatuses =
                    subject.type.statuses.filter { it != SubjectInterestStatus.MARK_STATUS_UNMARK }
                interestStatuses.forEachIndexed { index, status ->
                    SegmentedButton(
                        selected = status == subject.interest?.status,
                        onClick = {
                            if (status in setOf(
                                    SubjectInterestStatus.MARK_STATUS_DOING,
                                    SubjectInterestStatus.MARK_STATUS_DONE
                                )
                            ) {
                                pendingStatus = status
                            } else {
                                onUpdateStatus(status, null)
                            }
                        },
                        shape = SegmentedButtonDefaults.doubeanItemShape(
                            index = index,
                            count = interestStatuses.size
                        ),
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
    onUpdateStatus: (newStatus: SubjectInterestStatus, rating: Int?) -> Unit,
) {
    DoubeanTextButton(onClick = {
        onUpdateStatus(SubjectInterestStatus.MARK_STATUS_UNMARK, null)
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

