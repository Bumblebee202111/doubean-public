package com.github.bumblebee202111.doubean.feature.subjects.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.feature.subjects.common.RankLists
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.component.SectionErrorWithRetry

@Composable
fun BooksScreen(
    onRankListClick: (collectionId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: BooksViewModel = hiltViewModel(),
) {
    val modulesUiState by viewModel.modulesUiState.collectAsStateWithLifecycle()
    BooksScreen(
        modifier = modifier,
        modulesUiState = modulesUiState,
        contentPadding = contentPadding,
        onRankListClick = onRankListClick,
        onBookClick = onBookClick,
        onRetryClick = viewModel::retry
    )
}

@Composable
fun BooksScreen(
    modifier: Modifier = Modifier,
    modulesUiState: SubjectModulesUiState,
    contentPadding: PaddingValues = PaddingValues(),
    onRankListClick: (collectionId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    onRetryClick: () -> Unit,
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                                            SubjectType.BOOK -> onBookClick(subject.id)
                                            else -> Unit 
                                        }
                                    }
                                )
                            }
                        }

                        else -> Unit
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