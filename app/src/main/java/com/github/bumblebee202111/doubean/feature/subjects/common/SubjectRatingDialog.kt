package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.theme.RatingActiveColor
import com.github.bumblebee202111.doubean.ui.theme.RatingInactiveColor

@Composable
fun SubjectRatingDialog(
    initialRating: Int?,
    onDismissRequest: () -> Unit,
    onConfirm: (rating: Int?) -> Unit,
) {
    var rating by remember { mutableIntStateOf(initialRating ?: 0) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.rate_subject_title)) },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    val isSelected = i <= rating
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (isSelected) RatingActiveColor else RatingInactiveColor.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false, radius = 24.dp)
                            ) {
                                rating = if (rating == i) 0 else i
                            }
                            .padding(4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(if (rating > 0) rating else null) }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

