package com.github.bumblebee202111.doubean.feature.subjects.movie

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.Movie
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.SubjectDetailHeader
import com.github.bumblebee202111.doubean.ui.SubjectTopBar

@Composable
fun MovieScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: MovieViewModel = hiltViewModel(),
) {
    val movieUiState by viewModel.movieUiState.collectAsStateWithLifecycle()
    MovieScreen(
        movieUiState = movieUiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus
    )
}

@Composable
fun MovieScreen(
    movieUiState: MovieUiState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (subject: SubjectWithInterest<Movie>, newStatus: SubjectInterest.Status) -> Unit,
) {
    Scaffold(
        topBar = {
            MovieTopBar(movieUiState = movieUiState, onBackClick = onBackClick)
        }
    ) { innerPadding ->
        when (movieUiState) {
            is MovieUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    item {
                        SubjectDetailHeader(
                            subject = movieUiState.movie,
                            isLoggedIn = movieUiState.isLoggedIn,
                            onLoginClick = onLoginClick,
                            onUpdateStatus = onUpdateStatus
                        )
                    }
                }
            }

            else -> {

            }
        }
    }
}

@Composable
private fun MovieTopBar(movieUiState: MovieUiState, onBackClick: () -> Unit) {
    SubjectTopBar(
        subjectType = SubjectType.MOVIE,
        subject = (movieUiState as? MovieUiState.Success)?.movie,
        onBackClick = onBackClick
    )
}
