package com.github.bumblebee202111.doubean.feature.subjects.book

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectDetailHeader
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectReviewsSheetContent
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectScaffold
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectTopBar
import com.github.bumblebee202111.doubean.feature.subjects.shared.subjectInfoInterestsModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.shared.subjectInfoIntroModuleItem
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

@Composable
fun BookScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    viewModel: BookViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BookScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus,
        onImageClick = onImageClick,
        onUserClick = onUserClick
    )
}

@Composable
fun BookScreen(
    uiState: BookUiState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
) {
    SubjectScaffold(
        reviewsSheetContent = {
            if (uiState is BookUiState.Success) {
                SubjectReviewsSheetContent(
                    subjectType = SubjectType.BOOK,
                    reviews = uiState.reviews,
                    onUserClick = onUserClick
                )

            }
        },
        topBar = {
            BookTopBar(uiState = uiState, onBackClick = onBackClick)
        }
    ) { innerPadding ->
        when (uiState) {
            is BookUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    with(uiState) {
                        item {
                            SubjectDetailHeader(
                                subject = book,
                                isLoggedIn = isLoggedIn,
                                onLoginClick = onLoginClick,
                                onUpdateStatus = onUpdateStatus,
                                onImageClick = onImageClick
                            )
                        }
                        subjectInfoIntroModuleItem(book.intro)
                        subjectInfoInterestsModuleItem(interests, onUserClick)
                    }
                }
            }

            else -> {

            }
        }
    }
}

@Composable
private fun BookTopBar(uiState: BookUiState, onBackClick: () -> Unit) {
    SubjectTopBar(
        subjectType = SubjectType.BOOK,
        subject = (uiState as? BookUiState.Success)?.book,
        onBackClick = onBackClick
    )
}
