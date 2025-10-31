package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest

@Composable
fun <T : Subject> MySubjectItem(
    subject: SubjectWithInterest<T>,
    isLoggedIn: Boolean,
    onClick: () -> Unit,
    onUpdateStatus: (subject: SubjectWithInterest<T>, newStatus: SubjectInterestStatus) -> Unit,
) {
    var showActionsBottomSheet by remember { mutableStateOf(false) }

    if (showActionsBottomSheet && isLoggedIn) {
        MySubjectActionsBottomSheet(
            subject = subject,
            onUpdateStatus = { newStatus ->
                onUpdateStatus(subject, newStatus)
            },
            onDismissRequest = { showActionsBottomSheet = false }
        )
    }

    Box(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick)
    ) {
        SimpleSubjectRowItemContent(subject = subject.subject)
        if (isLoggedIn) {
            IconButton(
                onClick = { showActionsBottomSheet = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more)
                )
            }
        }
    }

}

@Composable
fun MySubjectItemMore(moreCount: Int, onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "+$moreCount",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
@Preview
fun MySubjectItemMorePreview() {
    MySubjectItemMore(100)
}