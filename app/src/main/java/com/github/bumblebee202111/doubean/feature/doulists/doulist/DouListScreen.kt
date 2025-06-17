package com.github.bumblebee202111.doubean.feature.doulists.doulist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.doulists.common.douListPostItems
import com.github.bumblebee202111.doubean.feature.doulists.common.getDouListSubtitle
import com.github.bumblebee202111.doubean.feature.doulists.common.rememberFeedItemClickHandler
import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.doulists.DouList
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.FullScreenCenteredContent

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
    DouListScreen(
        uiState = uiState,
        isLoggedIn = isLoggedIn,
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onBookClick = onBookClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onMarkSubject = viewModel::markSubject
    )
}

@Composable
fun DouListScreen(
    uiState: DouListUiState,
    isLoggedIn: Boolean,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onMarkSubject: (MarkableSubject) -> Unit,
) {
    Scaffold(
        topBar = {
            DouListTopAppBar(
                douList = uiState.douList,
                onBackClick = onBackClick
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
                FullScreenCenteredContent(paddingValues = innerPadding) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                FullScreenCenteredContent(paddingValues = innerPadding) {
                    Text(
                        text = uiState.errorMessage.getString(),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            else -> {
                FullScreenCenteredContent(paddingValues = innerPadding) {
                    Text(
                        text = stringResource(R.string.empty_content_title),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DouListTopAppBar(
    douList: DouList?,
    onBackClick: () -> Unit,
) {
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
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            
            
            
            
            
            
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

