package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Subject> MySubjectActionsBottomSheet(
    subject: SubjectWithInterest<T>,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier.padding(16.dp)) {
            SubjectInterestButtons(
                subject = subject,
                onUpdateStatus = { newStatus ->
                    onDismissRequest()
                    onUpdateStatus(newStatus)
                }
            )
        }
    }
}