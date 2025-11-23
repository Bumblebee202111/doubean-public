package com.github.bumblebee202111.doubean.feature.subjects.ranklist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.common.rankList
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator

@Composable
fun RankListScreen(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    viewModel: RankListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.rankListUiState.collectAsStateWithLifecycle()
    val items = viewModel.itemsPagingData.collectAsLazyPagingItems()
    RankListScreen(
        uiState = uiState,
        items = items,
        onMarkSubject = viewModel::onMarkSubject,
        onBackClick = onBackClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onBookClick = onBookClick,
        onRetryClick = viewModel::retry
    )
}

@Composable
fun RankListScreen(
    uiState: RankListUiState,
    items: LazyPagingItems<SubjectWithRankAndInterest<out Subject>>,
    onMarkSubject: (subject: SubjectWithRankAndInterest<*>) -> Unit,
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    onRetryClick: () -> Unit,
) {
    Scaffold(topBar = {
        RankListTopBar(onBackClick = onBackClick)
    }) { innerPadding ->
        when (uiState) {
            is RankListUiState.Error -> {
                FullScreenErrorWithRetry(
                    message = uiState.message.getString(),
                    onRetryClick = onRetryClick,
                    contentPadding = innerPadding
                )
            }

            RankListUiState.Loading -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            is RankListUiState.Success -> {
                LazyColumn(Modifier.fillMaxWidth(), contentPadding = innerPadding) {
                    with(uiState) {
                        rankList(
                            subjectCollection = rankList,
                            subjectCollectionItems = items,
                            isLoggedIn = isLoggedIn,
                            onMovieClick = onMovieClick,
                            onTvClick = onTvClick,
                            onBookClick = onBookClick,
                            onMarkClick = onMarkSubject
                        )
                    }

                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankListTopBar(onBackClick: () -> Unit) {
    DoubeanTopAppBar(
        titleResId = R.string.title_subject_rank_list,
        navigationIcon = {
            BackButton(onClick = onBackClick)
        }
    )
}