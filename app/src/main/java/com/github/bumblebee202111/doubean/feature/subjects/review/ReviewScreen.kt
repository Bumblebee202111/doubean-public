package com.github.bumblebee202111.doubean.feature.subjects.review

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subject.ReviewDetail
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.ui.component.MoreButton
import com.github.bumblebee202111.doubean.util.OpenInUtils


@Composable
fun ReviewScreen(
    onBackClick: () -> Unit,
    onWebViewClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onOpenDeepLinkUrl: (String) -> Boolean,
    viewModel: ReviewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReviewScreen(
        uiState = uiState,
        displayInvalidImageUrl = viewModel::displayInvalidImageUrl,
        onBackClick = onBackClick,
        onWebViewClick = onWebViewClick,
        onImageClick = onImageClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        onRetryClick = viewModel::fetchReview
    )
}

@Composable
fun ReviewScreen(
    uiState: ReviewUiState,
    displayInvalidImageUrl: () -> Unit,
    onBackClick: () -> Unit,
    onWebViewClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onOpenDeepLinkUrl: (String) -> Boolean,
    onRetryClick: () -> Unit,
) {
    val context = LocalContext.current
    val review = (uiState as? ReviewUiState.Success)?.review

    Scaffold(
        topBar = {
            ReviewTopAppBar(
                review = review,
                onBackClick = onBackClick,
                onWebViewClick = onWebViewClick,
                onOpenInDoubanClick = { OpenInUtils.openInDouban(context, it) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState) {
            is ReviewUiState.Loading -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            is ReviewUiState.Error -> {
                FullScreenErrorWithRetry(
                    message = uiState.message.getString(),
                    onRetryClick = onRetryClick,
                    contentPadding = innerPadding
                )
            }

            is ReviewUiState.Success -> {
                ReviewContentWebView(
                    review = uiState.review,
                    innerPadding = innerPadding,
                    displayInvalidImageUrl = displayInvalidImageUrl,
                    onBackClick = onBackClick,
                    onImageClick = onImageClick,
                    onOpenDeepLinkUrl = onOpenDeepLinkUrl
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewTopAppBar(
    review: ReviewDetail?,
    onBackClick: () -> Unit,
    onWebViewClick: (String) -> Unit,
    onOpenInDoubanClick: (String) -> Unit,
) {
    var appBarMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var viewInMenuExpanded by rememberSaveable { mutableStateOf(false) }

    DoubeanTopAppBar(
        title = {},
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        actions = {
            MoreButton(onClick = { appBarMenuExpanded = !appBarMenuExpanded })

            DropdownMenu(
                expanded = appBarMenuExpanded,
                onDismissRequest = { appBarMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.view_in)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        appBarMenuExpanded = false
                        viewInMenuExpanded = true
                    }
                )
            }

            DropdownMenu(
                expanded = viewInMenuExpanded,
                onDismissRequest = { viewInMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.view_in_web)) },
                    onClick = {
                        viewInMenuExpanded = false
                        review?.url?.let(onWebViewClick)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.view_in_douban)) },
                    onClick = {
                        review?.uri?.let {
                            viewInMenuExpanded = false
                            onOpenInDoubanClick(it)
                        }
                    }
                )
            }
        }
    )
}