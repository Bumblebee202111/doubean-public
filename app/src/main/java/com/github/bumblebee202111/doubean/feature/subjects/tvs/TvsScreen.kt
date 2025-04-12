package com.github.bumblebee202111.doubean.feature.subjects.tvs

import androidx.compose.foundation.layout.Arrangement
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
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.feature.subjects.shared.MySubject
import com.github.bumblebee202111.doubean.feature.subjects.shared.RankLists
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectUnions
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.util.uiMessage

@Composable
fun TvsScreen(
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TvsViewModel = hiltViewModel(),
) {
    val myMoviesUiState by viewModel.myMoviesUiState.collectAsStateWithLifecycle()
    val tvsUiState by viewModel.modulesUiState.collectAsStateWithLifecycle()
    val uiErrors by viewModel.uiErrors.collectAsStateWithLifecycle()
    TvsScreen(
        myMoviesUiState = myMoviesUiState,
        modulesUiState = tvsUiState,
        uiErrors = uiErrors,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onRankListClick = onRankListClick,
        onTvClick = onTvClick,
        onErrorShown = viewModel::onErrorShown,
        onShowSnackbar = onShowSnackbar,
        modifier = modifier
    )
}

@Composable
fun TvsScreen(
    myMoviesUiState: MySubjectUiState,
    modulesUiState: SubjectModulesUiState,
    uiErrors: List<AppError>,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
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


    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            MySubject(
                mySubjectUiState = myMoviesUiState,
                onStatusClick = onSubjectStatusClick,
                onLoginClick = onLoginClick
            )
        }
        when (modulesUiState) {
            is SubjectModulesUiState.Success -> {
                modulesUiState.modules.forEach { module ->
                    when (module) {
                        is SubjectModule.SelectedCollections -> {
                            item {
                                RankLists(
                                    rankLists = module.selectedCollections,
                                    onRankListClick = onRankListClick,
                                    onSubjectClick = { subject ->
                                        when (subject.type) {
                                            SubjectType.TV -> onTvClick(subject.id)
                                            else -> Unit //impossible
                                        }
                                    }
                                )
                            }
                        }

                        is SubjectModule.SubjectUnions ->
                            item {
                                SubjectUnions(
                                    module = module,
                                    onSubjectClick = { subject ->
                                        when (subject.type) {
                                            SubjectType.TV -> onTvClick(subject.id)
                                            else -> Unit //impossible
                                        }
                                    }
                                )
                            }
                    }
                }
            }

            SubjectModulesUiState.Loading -> {
                //TODO
            }

            is SubjectModulesUiState.Error -> {
                //TODO
            }
        }
    }
}

