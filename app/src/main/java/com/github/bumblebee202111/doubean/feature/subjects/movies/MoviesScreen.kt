package com.github.bumblebee202111.doubean.feature.subjects.movies

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.feature.subjects.shared.SearchSubjectButton
import com.github.bumblebee202111.doubean.feature.subjects.shared.mySubject
import com.github.bumblebee202111.doubean.feature.subjects.shared.rankLists
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.SubjectModule
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectsSearchType
import com.github.bumblebee202111.doubean.util.uiMessage

@Composable
fun MoviesScreen(
    onLoginClick: () -> Unit,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onSearchClick: (type: SubjectsSearchType) -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoviesViewModel = hiltViewModel(),
) {
    val myMoviesUiState by viewModel.myMoviesUiState.collectAsStateWithLifecycle()
    val modulesUiState by viewModel.modulesUiState.collectAsStateWithLifecycle()
    val uiErrors by viewModel.uiErrors.collectAsStateWithLifecycle()
    MoviesScreen(
        myMoviesUiState = myMoviesUiState,
        modulesUiState = modulesUiState,
        uiErrors = uiErrors,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onSearchClick = onSearchClick,
        onRankListClick = onRankListClick,
        onMovieClick = onMovieClick,
        onErrorShown = viewModel::onErrorShown,
        onShowSnackbar = onShowSnackbar,
        modifier = modifier
    )
}

@Composable
fun MoviesScreen(
    myMoviesUiState: MySubjectUiState,
    modulesUiState: SubjectModulesUiState,
    uiErrors: List<AppError>,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onSearchClick: (type: SubjectsSearchType) -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onErrorShown: (AppError) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiErrors.isNotEmpty()) {
        val error = remember(uiErrors) {
            uiErrors[0]
        }
        val message = error.uiMessage
        LaunchedEffect(error) {
            onShowSnackbar(message)
            onErrorShown(error)
        }
        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            onErrorShown(error)
        }
    }

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
        when (modulesUiState) {
            is SubjectModulesUiState.Success -> {
                modulesUiState.modules.forEach { module ->
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

                                        else -> Unit 
                                    }
                                }
                            )
                        }
                    }
                }
            }

            SubjectModulesUiState.Loading -> {
                
            }

            is SubjectModulesUiState.Error -> Unit
        }
    }
}

