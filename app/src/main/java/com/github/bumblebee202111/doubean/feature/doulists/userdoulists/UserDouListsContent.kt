package com.github.bumblebee202111.doubean.feature.doulists.userdoulists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.component.FullScreenCenteredContent

@Composable
fun UserDouListsContent(
    uiState: UserDouListsUiState,
    onItemClick: (douListId: String) -> Unit,
    onRetryClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    when (uiState) {
        is UserDouListsUiState.Loading -> {
            FullScreenCenteredContent(contentPadding) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is UserDouListsUiState.Error -> {
            FullScreenCenteredContent(contentPadding) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.message.getString())
                    Button(onClick = onRetryClick, modifier = Modifier.padding(top = 8.dp)) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }

        is UserDouListsUiState.Success -> {
            if (uiState.douLists.douLists.isEmpty()) {
                FullScreenCenteredContent(contentPadding) {
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
                    contentPadding = contentPadding
                )
            }
        }
    }
}