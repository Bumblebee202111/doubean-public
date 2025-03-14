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
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.feature.subjects.shared.SearchSubjectButton
import com.github.bumblebee202111.doubean.feature.subjects.shared.mySubject
import com.github.bumblebee202111.doubean.feature.subjects.shared.rankLists
import com.github.bumblebee202111.doubean.model.SubjectModule
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectsSearchType

@Composable
fun MoviesScreen(
    onLoginClick: () -> Unit,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onSearchClick: (type: SubjectsSearchType) -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
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
        onSearchClick = onSearchClick,
        onRankListClick = onRankListClick,
        onMovieClick = onMovieClick,
        modifier = modifier
    )
}

@Composable
fun MoviesScreen(
    myMoviesUiState: MySubjectUiState,
    moviesUiState: MoviesUiState,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onSearchClick: (type: SubjectsSearchType) -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item {
            SearchSubjectButton(
                onClick = { onSearchClick(SubjectsSearchType.MOVIES_AND_TVS) },
                hintRes = R.string.search_movies_and_tvs_hint
            )
        }
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
                moviesUiState.modules.forEach { module ->
                    when (module) {
                        is SubjectModule.SelectedCollections -> {
                            rankLists(
                                rankLists = module.selectedCollections,
                                onRankListClick = onRankListClick,
                                onSubjectClick = { subject ->
                                    when (subject.type) {
                                        SubjectType.MOVIE -> {
                                            onMovieClick(subject.id)
                                        }

                                        else -> Unit //impossible
                                    }
                                }
                            )
                        }
                    }
                }
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

