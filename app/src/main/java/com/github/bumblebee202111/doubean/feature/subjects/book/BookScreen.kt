package com.github.bumblebee202111.doubean.feature.subjects.book

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.ui.SubjectDetailHeader
import com.github.bumblebee202111.doubean.ui.SubjectInfoReviewsModuleItemContent
import com.github.bumblebee202111.doubean.ui.SubjectTopBar
import com.github.bumblebee202111.doubean.ui.subjectInfoInterestsModuleItem
import com.github.bumblebee202111.doubean.ui.subjectInfoIntroModuleItem

@Composable
fun BookScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: BookViewModel = hiltViewModel(),
) {
    val bookUiState by viewModel.bookUiState.collectAsStateWithLifecycle()
    BookScreen(
        bookUiState = bookUiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    bookUiState: BookUiState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
) {
    BottomSheetScaffold(
        sheetContent = {
            if (bookUiState is BookUiState.Success) {
                SubjectInfoReviewsModuleItemContent(
                    subjectType = SubjectType.BOOK,
                    reviews = bookUiState.reviews,
                    modifier = Modifier.fillMaxHeight(0.618f)
                )

            }
        },
        sheetPeekHeight = 128.dp,
        topBar = {
            BookTopBar(bookUiState = bookUiState, onBackClick = onBackClick)
        }
    ) { innerPadding ->
        when (bookUiState) {
            is BookUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    with(bookUiState) {
                        item {
                            SubjectDetailHeader(
                                subject = book,
                                isLoggedIn = isLoggedIn,
                                onLoginClick = onLoginClick,
                                onUpdateStatus = onUpdateStatus
                            )
                        }
                        subjectInfoIntroModuleItem(book.intro)
                        subjectInfoInterestsModuleItem(interests)
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
