package com.github.bumblebee202111.doubean.feature.subjects.ranklist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.SubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.rankList

@Composable
fun RankListScreen(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    viewModel: RankListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.rankListUiState.collectAsStateWithLifecycle()
    RankListScreen(
        uiState = uiState,
        onMarkSubject = viewModel::onMarkSubject,
        onBackClick = onBackClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onBookClick = onBookClick,
    )
}

@Composable
fun RankListScreen(
    uiState: RankListUiState,
    onMarkSubject: (subject: SubjectWithRankAndInterest<*>) -> Unit,
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    Scaffold(topBar = {
        RankListTopBar(onBackClick = onBackClick)
    }) {
        when (uiState) {
            RankListUiState.Error -> {
                /* TODO */
            }

            RankListUiState.Loading -> {
                /* TODO */
            }

            is RankListUiState.Success -> {
                LazyColumn(Modifier.fillMaxWidth(), contentPadding = it) {
                    with(uiState) {
                        rankList(
                            subjectCollection = rankList,
                            items = items,
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
    DoubeanTopAppBar(titleResId = R.string.title_subject_rank_list, navigationIcon = {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
        }
    })
}