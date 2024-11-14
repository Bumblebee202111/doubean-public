package com.github.bumblebee202111.doubean.feature.subjects.books

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
fun BooksScreen(
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BooksViewModel = hiltViewModel(),
) {
    val myBooksUiState by viewModel.myBooksUiState.collectAsStateWithLifecycle()
    val booksUiState by viewModel.booksUiState.collectAsStateWithLifecycle()
    BooksScreen(
        myBooksUiState = myBooksUiState,
        booksUiState = booksUiState,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onMarkClick = viewModel::onMarkBook,
        modifier = modifier
    )
}

@Composable
fun BooksScreen(
    myBooksUiState: MySubjectUiState,
    booksUiState: BooksUiState,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onMarkClick: (book: SubjectWithInterest) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        mySubject(
            mySubjectUiState = myBooksUiState,
            onStatusClick = onSubjectStatusClick,
            onLoginClick = onLoginClick
        )
        item {
            Spacer(modifier = Modifier.size(16.dp))
        }
        when (booksUiState) {
            is BooksUiState.Success -> {
                subjectCollection(
                    title = booksUiState.title,
                    items = booksUiState.items,
                    isLoggedIn = booksUiState.isLoggedIn,
                    onMarkClick = onMarkClick
                )
            }

            BooksUiState.Loading -> {
                //TODO
            }

            BooksUiState.Error -> {
                //TODO
            }

        }
    }
}