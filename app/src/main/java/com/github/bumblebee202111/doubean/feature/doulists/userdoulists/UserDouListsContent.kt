package com.github.bumblebee202111.doubean.feature.doulists.userdoulists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.component.FullScreenCenteredContent
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator

@Composable
fun UserDouListsContent(
    uiState: UserDouListsUiState,
    onItemClick: (douListId: String) -> Unit,
    onRetryClick: () -> Unit,
    innerPadding: PaddingValues = PaddingValues(),
) {
    when (uiState) {
        is UserDouListsUiState.Loading -> {
            FullScreenLoadingIndicator(contentPadding = innerPadding)
        }

        is UserDouListsUiState.Error -> {
            FullScreenErrorWithRetry(
                message = uiState.message.getString(),
                contentPadding = innerPadding,
                onRetryClick = onRetryClick
            )
        }

        is UserDouListsUiState.Success -> {
            if (uiState.douLists.douLists.isEmpty()) {
                FullScreenCenteredContent(innerPadding) {
                    Text(
                        text = stringResource(R.string.empty_content_title),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 16.dp)
                    )
                }
            } else {
                UserDouLists(
                    douLists = uiState.douLists.douLists,
                    onItemClick = onItemClick,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding
                )
            }
        }
    }
}