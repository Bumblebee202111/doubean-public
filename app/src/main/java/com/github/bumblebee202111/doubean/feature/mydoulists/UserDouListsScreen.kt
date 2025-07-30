package com.github.bumblebee202111.doubean.feature.mydoulists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.doulists.userdoulists.UserDouListsContent
import com.github.bumblebee202111.doubean.feature.doulists.userdoulists.UserDouListsUiState

@Composable
fun UserDouListsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onDouListClick: (douListId: String) -> Unit,
    viewModel: UserDouListsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserDouListsScreen(
        uiState = uiState,
        onDouListClick = onDouListClick,
        onRetryClick = viewModel::onRetry,
        contentPadding = contentPadding
    )
}

@Composable
fun UserDouListsScreen(
    uiState: UserDouListsUiState,
    onDouListClick: (douListId: String) -> Unit,
    onRetryClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    UserDouListsContent(
        uiState = uiState,
        onItemClick = onDouListClick,
        onRetryClick = onRetryClick,
        contentPadding = contentPadding
    )
}