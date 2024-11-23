package com.github.bumblebee202111.doubean.feature.subjects.books

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.SubjectsSearchType
import com.github.bumblebee202111.doubean.ui.mySubject
import com.github.bumblebee202111.doubean.ui.subjectCollection

@Composable
fun BooksScreen(
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BooksViewModel = hiltViewModel(),
    onSearchClick: (type: SubjectsSearchType) -> Unit,
) {
    val myBooksUiState by viewModel.myBooksUiState.collectAsStateWithLifecycle()
    val booksUiState by viewModel.booksUiState.collectAsStateWithLifecycle()
    BooksScreen(
        myBooksUiState = myBooksUiState,
        booksUiState = booksUiState,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onMarkClick = viewModel::onMarkBook,
        onSearchClick = onSearchClick,
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
    onSearchClick: (type: SubjectsSearchType) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item {
            OutlinedButton(
                onClick = { onSearchClick(SubjectsSearchType.BOOKS) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                Text(text = stringResource(id = R.string.search_books_hint))
            }
        }
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
                
            }

            BooksUiState.Error -> {
                
            }

        }
    }
}