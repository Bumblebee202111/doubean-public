package com.github.bumblebee202111.doubean.feature.subjects.tvs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.feature.subjects.common.MySubject
import com.github.bumblebee202111.doubean.feature.subjects.common.RankLists
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectUnions
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.component.SectionErrorWithRetry

@Composable
fun TvsScreen(
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TvsViewModel = hiltViewModel(),
) {
    val myMoviesUiState by viewModel.myTvsUiState.collectAsStateWithLifecycle()
    val tvsUiState by viewModel.modulesUiState.collectAsStateWithLifecycle()
    TvsScreen(
        modifier = modifier,
        myMoviesUiState = myMoviesUiState,
        modulesUiState = tvsUiState,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onRankListClick = onRankListClick,
        onTvClick = onTvClick,
        onRetryClick = viewModel::retry
    )
}

@Composable
fun TvsScreen(
    modifier: Modifier = Modifier,
    myMoviesUiState: MySubjectUiState,
    modulesUiState: SubjectModulesUiState,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onRetryClick: () -> Unit,
) {
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
                                            else -> Unit 
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
                                            else -> Unit 
                                        }
                                    }
                                )
                            }
                    }
                }
            }

            SubjectModulesUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is SubjectModulesUiState.Error -> {
                item {
                    SectionErrorWithRetry(
                        message = modulesUiState.message.getString(),
                        onRetryClick = onRetryClick
                    )
                }
            }
        }
    }
}

