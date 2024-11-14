package com.github.bumblebee202111.doubean.feature.subjects.movies

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.mySubject
import com.github.bumblebee202111.doubean.ui.subjectCollection

@Composable
fun MoviesScreen(
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoviesViewModel = hiltViewModel(),
) {
    val myMoviesUiState by viewModel.myMoviesUiState.collectAsStateWithLifecycle()
    val moviesUiState by viewModel.moviesUiState.collectAsStateWithLifecycle()
    MoviesScreen(
        myMoviesUiState = myMoviesUiState,
        moviesUiState = moviesUiState,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onMarkClick = viewModel::onMarkMovie,
        modifier = modifier
    )
}

@Composable
fun MoviesScreen(
    myMoviesUiState: MySubjectUiState,
    moviesUiState: MoviesUiState,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onMarkClick: (movie: SubjectWithInterest) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        mySubject(
            mySubjectUiState = myMoviesUiState,
            onStatusClick = onSubjectStatusClick,
            onLoginClick = onLoginClick
        )
        item {
            Spacer(modifier = Modifier.size(16.dp))
        }
        when (moviesUiState) {
            is MoviesUiState.Success -> {
                subjectCollection(
                    title = moviesUiState.title,
                    items = moviesUiState.items,
                    isLoggedIn = moviesUiState.isLoggedIn,
                    onMarkClick = onMarkClick
                )
            }

            MoviesUiState.Loading -> {
                //TODO
            }

            MoviesUiState.Error -> {
                //TODO
            }
        }
    }
}