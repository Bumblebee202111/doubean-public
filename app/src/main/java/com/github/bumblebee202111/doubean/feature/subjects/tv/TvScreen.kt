package com.github.bumblebee202111.doubean.feature.subjects.tv

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.common.InterestSortType
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectDetailHeader
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectReviewsSheetContent
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectScaffold
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectTopBar
import com.github.bumblebee202111.doubean.feature.subjects.common.rememberRecommendSubjectClickHandler
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoCelebritiesModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoInterestsModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoIntroModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoRecommendModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoTrailersModuleItem
import com.github.bumblebee202111.doubean.model.doulists.ItemDouList
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.common.CollectDialogUiState
import com.github.bumblebee202111.doubean.ui.common.CreateDouListDialog
import com.github.bumblebee202111.doubean.ui.common.DouListDialog
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator

@Composable
fun TvScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    viewModel: TvViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val collectDialogUiState by viewModel.collectDialogUiState.collectAsStateWithLifecycle()
    val showCreateDouListDialog by viewModel.showCreateDouListDialog.collectAsStateWithLifecycle()

    TvScreen(
        uiState = uiState,
        collectDialogUiState = collectDialogUiState,
        showCreateDouListDialog = showCreateDouListDialog,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::updateStatus,
        onImageClick = onImageClick,
        onUserClick = onUserClick,
        onRecommendSubjectClick = rememberRecommendSubjectClickHandler(
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick
        ),
        onCollectClick = viewModel::collect,
        onDismissCollectDialog = viewModel::dismissCollectDialog,
        onToggleCollection = viewModel::toggleCollection,
        onCreateDouList = viewModel::showCreateDialog,
        onDismissCreateDialog = viewModel::dismissCreateDialog,
        onCreateAndCollect = viewModel::createAndCollect,
        onToggleInterestSort = viewModel::toggleInterestSortType,
        onRetryClick = viewModel::refreshData
    )
}

@Composable
fun TvScreen(
    uiState: TvUiState,
    collectDialogUiState: CollectDialogUiState?,
    showCreateDouListDialog: Boolean,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onRecommendSubjectClick: (subject: RecommendSubject) -> Unit,
    onCollectClick: () -> Unit,
    onDismissCollectDialog: () -> Unit,
    onToggleCollection: (douList: ItemDouList) -> Unit,
    onCreateDouList: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onCreateAndCollect: (title: String) -> Unit,
    onToggleInterestSort: (InterestSortType) -> Unit,
    onRetryClick: () -> Unit,
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

    val context = LocalContext.current

    SubjectScaffold(
        reviewsSheetContent = {
            if (uiState is TvUiState.Success) {
                SubjectReviewsSheetContent(
                    subjectType = SubjectType.TV,
                    reviews = uiState.reviews,
                    onUserClick = onUserClick,
                )

            }
        },
        topBar = {
            TvTopBar(uiState = uiState, onBackClick = onBackClick, onCollectClick = onCollectClick)
        }
    ) { innerPadding ->
        when (uiState) {
            is TvUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    with(uiState) {
                        item {
                            SubjectDetailHeader(
                                subject = tv,
                                isLoggedIn = isLoggedIn,
                                onLoginClick = onLoginClick,
                                onUpdateStatus = onUpdateStatus,
                                onImageClick = onImageClick
                            )
                        }
                        subjectInfoIntroModuleItem(intro = tv.intro)
                        subjectInfoInterestsModuleItem(
                            interestList = interests,
                            onUserClick = onUserClick,
                            sortType = interestSortType,
                            onSortChange = onToggleInterestSort
                        )
                        subjectInfoCelebritiesModuleItem(
                            directorNames = tv.directorNames,
                            actorNames = tv.actorNames
                        )
                        subjectInfoTrailersModuleItem(
                            trailers = tv.trailers,
                            photoList = photos,
                            onImageClick = onImageClick,
                            onTrailerClick = { trailer ->
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(Uri.parse(trailer.videoUrl), "video/*")
                                }
                                context.startActivity(intent)
                            }
                        )
                        subjectInfoRecommendModuleItem(
                            subjectType = SubjectType.TV,
                            recommendations = recommendations,
                            onRecommendSubjectClick = onRecommendSubjectClick
                        )
                    }
                }
            }

            is TvUiState.Loading -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            is TvUiState.Error -> {
                FullScreenErrorWithRetry(
                    message = uiState.message.getString(),
                    onRetryClick = onRetryClick,
                    contentPadding = innerPadding
                )
            }
        }
    }
}

@Composable
private fun TvTopBar(uiState: TvUiState, onBackClick: () -> Unit, onCollectClick: () -> Unit) {
    SubjectTopBar(
        subjectType = SubjectType.TV,
        subject = (uiState as? TvUiState.Success)?.tv,
        isLoggedIn = (uiState as? TvUiState.Success)?.isLoggedIn == true,
        onBackClick = onBackClick,
        onCollectClick = onCollectClick
    )
}
