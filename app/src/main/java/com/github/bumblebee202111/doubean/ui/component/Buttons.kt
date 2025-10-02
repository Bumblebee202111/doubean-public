package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * A standardized, reusable IconButton for displaying informational tooltips or dialogs.
 *
 * @param onClick The lambda to be invoked when the button is clicked.
 * @param modifier The modifier to be applied to the IconButton.
 * @param contentDescription The accessibility text for the button.
 * @param enabled Whether the button is interactive.
 * @param tint The tint color for the icon when it is enabled.
 */
@Composable
fun InfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = contentDescription,
            tint = if (enabled) tint else Color.Unspecified
        )
    }
}