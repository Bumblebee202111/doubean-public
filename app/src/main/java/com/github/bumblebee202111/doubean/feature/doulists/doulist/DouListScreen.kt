package com.github.bumblebee202111.doubean.feature.doulists.doulist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.doulists.common.douListPostItems
import com.github.bumblebee202111.doubean.feature.doulists.common.getDouListLabel
import com.github.bumblebee202111.doubean.feature.doulists.common.getDouListSubtitle
import com.github.bumblebee202111.doubean.feature.doulists.common.rememberFeedItemClickHandler
import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.doulists.DouList
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.ui.component.MoreButton

@Composable
fun DouListScreen(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    viewModel: DouListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val showEditDialog by viewModel.showEditDialog.collectAsStateWithLifecycle()
    DouListScreen(
        uiState = uiState,
        isLoggedIn = isLoggedIn,
        showEditDialog = showEditDialog,
        onBackClick = onBackClick,
        onShowEditDialog = viewModel::onShowEditDialog,
        onDismissEditDialog = viewModel::onDismissEditDialog,
        onUpdateTitle = viewModel::updateTitle,
        onTopicClick = onTopicClick,
        onBookClick = onBookClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onMarkSubject = viewModel::markSubject,
        onRetryClick = viewModel::fetchDouListDetails
    )
}

@Composable
fun DouListScreen(
    uiState: DouListUiState,
    isLoggedIn: Boolean,
    showEditDialog: Boolean,
    onBackClick: () -> Unit,
    onShowEditDialog: () -> Unit,
    onDismissEditDialog: () -> Unit,
    onUpdateTitle: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onMarkSubject: (MarkableSubject) -> Unit,
    onRetryClick: () -> Unit,
) {

    if (showEditDialog && uiState.douList != null) {
        val douListLabel = getDouListLabel(category = uiState.douList.category)

        EditDouListDialog(
            initialTitle = uiState.douList.title,
            douListLabel = douListLabel,
            onDismiss = onDismissEditDialog,
            onConfirm = onUpdateTitle
        )
    }

    Scaffold(
        topBar = {
            DouListTopAppBar(
                douList = uiState.douList,
                isOwner = uiState.isOwner,
                onBackClick = onBackClick,
                onShowEditDialog = onShowEditDialog
            )
        }
    ) { innerPadding ->

        when {
            uiState.douList != null -> {
                DouListContent(
                    douList = uiState.douList,
                    items = uiState.items,
                    isLoadingMore = uiState.isLoading,
                    isLoggedIn = isLoggedIn,
                    scaffoldPadding = innerPadding,
                    onTopicClick = onTopicClick,
                    onBookClick = onBookClick,
                    onMovieClick = onMovieClick,
                    onTvClick = onTvClick,
                    onUserClick = onUserClick,
                    onImageClick = onImageClick,
                    onMarkSubject = onMarkSubject
                )
            }

            uiState.isLoading -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            uiState.errorMessage != null -> {
                FullScreenErrorWithRetry(
                    message = uiState.errorMessage.getString(),
                    contentPadding = innerPadding,
                    onRetryClick = onRetryClick
                )
            }

            else -> {
                FullScreenErrorWithRetry(
                    message = stringResource(R.string.empty_content_title),
                    contentPadding = innerPadding,
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DouListTopAppBar(
    douList: DouList?,
    isOwner: Boolean,
    onBackClick: () -> Unit,
    onShowEditDialog: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    DoubeanTopAppBar(
        title = {
            Column {
                douList?.let {
                    Text(it.title)
                    Text(
                        text = getDouListSubtitle(
                            itemCount = it.itemCount,
                            category = it.category,
                            followersCount = it.followersCount
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        actions = {
            // IconButton(onClick = { /* TODO: Follow action */ }) {
            //     Icon(Icons.Default.Add, contentDescription = "Follow")
            // }
            if (isOwner) {
                MoreButton(onClick = { menuExpanded = true })
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit)) },
                        onClick = {
                            menuExpanded = false
                            onShowEditDialog()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun DouListContent(
    douList: DouList?,
    items: List<DouListPostItem>,
    isLoadingMore: Boolean,
    scaffoldPadding: PaddingValues,
    isLoggedIn: Boolean,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onMarkSubject: (MarkableSubject) -> Unit,
) {
    val onItemClick = rememberFeedItemClickHandler(
        onTopicClick = onTopicClick,
        onBookClick = onBookClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = scaffoldPadding,
    ) {
        douList?.let {
            item {
                DouListHeader(douList = it)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        douListPostItems(
            items = items,
            isLoggedIn = isLoggedIn,
            isLoadingMore = isLoadingMore,
            onItemClick = onItemClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onMarkSubject = onMarkSubject
        )
    }
}

@Composable
private fun EditDouListDialog(
    initialTitle: String,
    douListLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (newTitle: String) -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }

    val titleError = remember(title) {
        when {
            title.isBlank() -> R.string.doulist_name_cannot_empty
            title.length > 60 -> R.string.doulist_name_cannot_more
            else -> null // No error
        }
    }
    val isError = titleError != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.activity_edit_doulist_title, douListLabel)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text(stringResource(R.string.doulist_name_hint)) },
                singleLine = true,
                isError = isError,
                supportingText = {
                    if (isError) {
                        val errorMessage = if (titleError == R.string.doulist_name_cannot_more) {
                            stringResource(titleError, douListLabel, 60)
                        } else {
                            stringResource(titleError, douListLabel)
                        }
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title) },
                enabled = !isError && title != initialTitle
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}
