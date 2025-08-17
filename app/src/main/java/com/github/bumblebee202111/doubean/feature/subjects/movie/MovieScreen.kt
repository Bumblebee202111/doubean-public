package com.github.bumblebee202111.doubean.feature.subjects.movie

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectDetailHeader
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectReviewsSheetContent
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectScaffold
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectTopBar
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoCelebritiesModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoInterestsModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoIntroModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoTrailersModuleItem
import com.github.bumblebee202111.doubean.model.doulists.ItemDouList
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.common.CollectDialogUiState
import com.github.bumblebee202111.doubean.ui.common.CreateDouListDialog
import com.github.bumblebee202111.doubean.ui.common.DouListDialog

@Composable
fun MovieScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    viewModel: MovieViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val collectDialogUiState by viewModel.collectDialogUiState.collectAsStateWithLifecycle()
    val showCreateDouListDialog by viewModel.showCreateDouListDialog.collectAsStateWithLifecycle()

    MovieScreen(
        uiState = uiState,
        collectDialogUiState = collectDialogUiState,
        showCreateDouListDialog = showCreateDouListDialog,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::updateStatus,
        onImageClick = onImageClick,
        onUserClick = onUserClick,
        onCollectClick = viewModel::collect,
        onDismissCollectDialog = viewModel::dismissCollectDialog,
        onToggleCollection = viewModel::toggleCollection,
        onCreateDouList = viewModel::showCreateDialog,
        onDismissCreateDialog = viewModel::dismissCreateDialog,
        onCreateAndCollect = viewModel::createAndCollect,
    )
}

@Composable
fun MovieScreen(
    uiState: MovieUiState,
    collectDialogUiState: CollectDialogUiState?,
    showCreateDouListDialog: Boolean,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onCollectClick: () -> Unit,
    onDismissCollectDialog: () -> Unit,
    onToggleCollection: (douList: ItemDouList) -> Unit,
    onCreateDouList: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onCreateAndCollect: (title: String) -> Unit,
) {
    collectDialogUiState?.let { state ->
        DouListDialog(
            uiState = state,
            onDismissRequest = onDismissCollectDialog,
            onDouListClick = onToggleCollection,
            onCreateClick = onCreateDouList
        )
    }

    if (showCreateDouListDialog) {
        CreateDouListDialog(
            onDismissRequest = onDismissCreateDialog,
            onConfirm = onCreateAndCollect
        )
    }

    SubjectScaffold(
        reviewsSheetContent = {
            if (uiState is MovieUiState.Success) {
                SubjectReviewsSheetContent(
                    subjectType = SubjectType.MOVIE,
                    reviews = uiState.reviews,
                    onUserClick = onUserClick
                )
            }
        },
        topBar = {
            MovieTopBar(
                uiState = uiState,
                onBackClick = onBackClick,
                onCollectClick = onCollectClick
            )
        }
    ) { innerPadding ->

        when (uiState) {
            is MovieUiState.Success -> {
                LazyColumn(
                    contentPadding = innerPadding,
                ) {
                    with(uiState) {
                        item {
                            SubjectDetailHeader(
                                subject = movie,
                                isLoggedIn = isLoggedIn,
                                onLoginClick = onLoginClick,
                                onUpdateStatus = onUpdateStatus,
                                onImageClick = onImageClick
                            )
                        }
                        subjectInfoIntroModuleItem(intro = movie.intro)
                        subjectInfoInterestsModuleItem(
                            interestList = interests,
                            onUserClick = onUserClick
                        )
                        subjectInfoCelebritiesModuleItem(
                            directorNames = movie.directorNames,
                            actorNames = movie.actorNames
                        )
                        subjectInfoTrailersModuleItem(
                            trailers = movie.trailers,
                            photoList = photos,
                            onImageClick = onImageClick
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
private fun MovieTopBar(
    uiState: MovieUiState, onBackClick: () -> Unit, onCollectClick: () -> Unit,
) {
    SubjectTopBar(
        subjectType = SubjectType.MOVIE,
        subject = (uiState as? MovieUiState.Success)?.movie,
        isLoggedIn = (uiState as? MovieUiState.Success)?.isLoggedIn == true,
        onBackClick = onBackClick,
        onCollectClick = onCollectClick
    )
}
