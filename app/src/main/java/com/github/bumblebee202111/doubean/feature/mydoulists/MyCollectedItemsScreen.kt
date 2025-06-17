package com.github.bumblebee202111.doubean.feature.mydoulists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.doulists.common.douListPostItems
import com.github.bumblebee202111.doubean.feature.doulists.common.rememberFeedItemClickHandler
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject

@Composable
fun MyCollectedItemsScreen(
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onDouListClick: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: MyCollectedItemsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    MyCollectedItemsScreen(
        uiState = uiState,
        isLoggedIn = isLoggedIn,
        onTopicClick = onTopicClick,
        onBookClick = onBookClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onMarkSubject = viewModel::markSubject,
        onDouListClick = onDouListClick,
        contentPadding = contentPadding
    )
}

@Composable
fun MyCollectedItemsScreen(
    uiState: MyCollectedItemsUiState,
    isLoggedIn: Boolean,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onMarkSubject: (MarkableSubject) -> Unit,
    onDouListClick: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues()){
    val onItemClick = rememberFeedItemClickHandler(
        onTopicClick = onTopicClick,
        onBookClick = onBookClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        douListPostItems(
            items = uiState.items,
            isLoggedIn = isLoggedIn,
            isLoadingMore = uiState.isLoading,
            onItemClick = onItemClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onMarkSubject = onMarkSubject,
            onDouListClick = onDouListClick
        )
    }
}