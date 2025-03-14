package com.github.bumblebee202111.doubean.feature.subjects.tvs

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
fun TvsScreen(
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onSearchClick: (type: SubjectsSearchType) -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TvsViewModel = hiltViewModel(),
) {
    val myMoviesUiState by viewModel.myMoviesUiState.collectAsStateWithLifecycle()
    val tvsUiState by viewModel.tvsUiState.collectAsStateWithLifecycle()
    TvsScreen(
        myMoviesUiState = myMoviesUiState,
        tvsUiState = tvsUiState,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onSearchClick = onSearchClick,
        onRankListClick = onRankListClick,
        onTvClick = onTvClick,
        modifier = modifier
    )
}

@Composable
fun TvsScreen(
    myMoviesUiState: MySubjectUiState,
    tvsUiState: TvsUiState,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onSearchClick: (type: SubjectsSearchType) -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
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
        when (tvsUiState) {
            is TvsUiState.Success -> {
                tvsUiState.modules.forEach { module ->
                    when (module) {
                        is SubjectModule.SelectedCollections -> {
                            rankLists(
                                rankLists = module.selectedCollections,
                                onRankListClick = onRankListClick,
                                onSubjectClick = { subject ->
                                    when (subject.type) {
                                        SubjectType.TV -> {
                                            onTvClick(subject.id)
                                        }

                                        else -> Unit 
                                    }
                                }
                            )
                        }
                    }
                }
            }

            TvsUiState.Loading -> {
                
            }

            TvsUiState.Error -> {
                
            }
        }
    }
}

