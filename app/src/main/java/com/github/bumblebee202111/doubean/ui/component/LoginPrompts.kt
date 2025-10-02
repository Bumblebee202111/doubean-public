package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R

/**
 * A reusable composable for a full-screen empty state that prompts the user to log in.
 * Use this when the entire content of a screen is protected.
 *
 * @param message The specific reason why the user needs to log in.
 * @param onLoginClick The lambda to be invoked when the login button is clicked.
 */
@Composable
fun FullScreenLoginPrompt(
    message: String,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PersonOutline,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onLoginClick) {
            Text(stringResource(R.string.login_button))
        }
    }
}


/**
 * A reusable composable for an inline login prompt.
 * Use this to replace a specific interactive component (like action buttons) for guest users.
 *
 * @param promptText The text to display inside the button (e.g., "Log in to rate").
 * @param onLoginClick The lambda to be invoked when the button is clicked.
 */
@Composable
fun InlineLoginPrompt(
    promptText: String,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onLoginClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(promptText)
    }
}


/**
 * A reusable AlertDialog that prompts the user to log in when they attempt
 * an action that requires authentication.
 *
 * @param onDismissRequest Called when the user dismisses the dialog.
 * @param onLoginClick The lambda to be invoked when the "Log in" button is clicked.
 * @param title The title of the dialog.
 * @param message The specific reason why login is required for this action.
 */
@Composable
fun LoginPromptDialog(
    onDismissRequest: () -> Unit,
    onLoginClick: () -> Unit,
    title: String = stringResource(R.string.need_login_hint),
    message: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onLoginClick) {
                Text(stringResource(R.string.login_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}