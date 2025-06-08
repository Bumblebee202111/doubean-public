package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        content = {
            Surface(
                shape = RoundedCornerShape(28.0.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                        content()
                    }

                }
            }

        }
    )
}
