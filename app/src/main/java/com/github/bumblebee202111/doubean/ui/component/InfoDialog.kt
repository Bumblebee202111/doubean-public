package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.bumblebee202111.doubean.R

/**
 * A simple, generic AlertDialog for displaying a title, a body of text,
 * and a single "OK" confirmation button.
 *
 * @param onDismissRequest Called when the user dismisses the dialog.
 * @param title The title text of the dialog.
 * @param text The main body text of the dialog.
 */
@Composable
fun InfoDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.ok_button))
            }
        }
    )
}