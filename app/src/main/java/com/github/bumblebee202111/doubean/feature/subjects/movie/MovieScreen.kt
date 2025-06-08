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

    MovieScreen(
        uiState = uiState,
        collectDialogUiState = collectDialogUiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus,
        onImageClick = onImageClick,
        onUserClick = onUserClick,
        onCollectClick = viewModel::onCollectClick,
        dismissCollectDialog = viewModel::dismissCollectDialog,
        toggleCollectionInDouList = viewModel::toggleCollectionInDouList
    )
}

@Composable
fun MovieScreen(
    uiState: MovieUiState,
    collectDialogUiState: CollectDialogUiState?,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onCollectClick: () -> Unit,
    toggleCollectionInDouList: (douList: ItemDouList) -> Unit,
    dismissCollectDialog: () -> Unit,
) {
    collectDialogUiState?.let { state ->
        DouListDialog(
            uiState = state,
            onDismissRequest = dismissCollectDialog,
            onDouListClick = toggleCollectionInDouList
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
