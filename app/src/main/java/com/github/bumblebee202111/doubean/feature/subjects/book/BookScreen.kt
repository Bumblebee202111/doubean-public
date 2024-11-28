package com.github.bumblebee202111.doubean.feature.subjects.book

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.Book
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.SubjectDetailHeader
import com.github.bumblebee202111.doubean.ui.SubjectTopBar

@Composable
fun BookScreen(onBackClick: () -> Unit, viewModel: BookViewModel = hiltViewModel()) {
    val bookUiState by viewModel.bookUiState.collectAsStateWithLifecycle()
    BookScreen(
        bookUiState = bookUiState,
        onBackClick = onBackClick,
        onUpdateStatus = viewModel::onUpdateStatus
    )
}

@Composable
fun BookScreen(
    bookUiState: BookUiState,
    onBackClick: () -> Unit,
    onUpdateStatus: (subject: SubjectWithInterest<Book>, newStatus: SubjectInterest.Status) -> Unit,
) {
    Scaffold(
        topBar = {
            BookTopBar(bookUiState = bookUiState, onBackClick = onBackClick)
        }
    ) { innerPadding ->
        when (bookUiState) {
            is BookUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    item {
                        SubjectDetailHeader(
                            subject = bookUiState.book,
                            onUpdateStatus = onUpdateStatus
                        )
                    }
                }
            }

            else -> {

            }
        }
    }
}

@Composable
private fun BookTopBar(bookUiState: BookUiState, onBackClick: () -> Unit) {
    SubjectTopBar(
        subjectType = SubjectType.BOOK,
        subject = (bookUiState as? BookUiState.Success)?.book,
        onBackClick = onBackClick
    )
}
