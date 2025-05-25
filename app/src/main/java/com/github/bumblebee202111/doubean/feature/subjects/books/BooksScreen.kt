package com.github.bumblebee202111.doubean.feature.subjects.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.feature.subjects.common.MySubject
import com.github.bumblebee202111.doubean.feature.subjects.common.RankLists
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

@Composable
fun BooksScreen(
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BooksViewModel = hiltViewModel(),
) {
    val myBooksUiState by viewModel.myBooksUiState.collectAsStateWithLifecycle()
    val modulesUiState by viewModel.modulesUiState.collectAsStateWithLifecycle()
    BooksScreen(
        myBooksUiState = myBooksUiState,
        modulesUiState = modulesUiState,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onRankListClick = onRankListClick,
        onBookClick = onBookClick,
        modifier = modifier
    )
}

@Composable
fun BooksScreen(
    myBooksUiState: MySubjectUiState,
    modulesUiState: SubjectModulesUiState,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    modifier: Modifier = Modifier,
) {

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            MySubject(
                mySubjectUiState = myBooksUiState,
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
                                            SubjectType.BOOK -> onBookClick(subject.id)
                                            else -> Unit 
                                        }
                                    }
                                )
                            }
                        }

                        is SubjectModule.SubjectUnions -> Unit 
                    }
                }
            }

            SubjectModulesUiState.Loading -> {
                
            }

            is SubjectModulesUiState.Error -> {
                
            }

        }
    }
}