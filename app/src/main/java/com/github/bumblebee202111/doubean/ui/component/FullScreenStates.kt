package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R

@Composable
fun FullScreenLoadingIndicator(
    contentPadding: PaddingValues,
) {
    FullScreenCenteredContent(contentPadding = contentPadding) {
        CircularProgressIndicator()
    }
}

@Composable
fun FullScreenErrorWithRetry(
    message: String,
    contentPadding: PaddingValues,
    onRetryClick: () -> Unit,
) {
    FullScreenCenteredContent(contentPadding = contentPadding) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(
                onClick = onRetryClick,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}