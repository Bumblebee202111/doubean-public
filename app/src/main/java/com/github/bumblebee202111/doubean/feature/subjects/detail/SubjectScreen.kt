package com.github.bumblebee202111.doubean.feature.subjects.detail

import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
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
import com.github.bumblebee202111.doubean.model.subjects.BookDetail
import com.github.bumblebee202111.doubean.model.subjects.MovieDetail
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.model.subjects.TvDetail
import com.github.bumblebee202111.doubean.ui.common.CollectDialogUiState
import com.github.bumblebee202111.doubean.ui.common.CreateDouListDialog
import com.github.bumblebee202111.doubean.ui.common.DouListDialog
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator

@Composable
fun SubjectScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
    viewModel: SubjectViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val collectDialogUiState by viewModel.collectDialogUiState.collectAsStateWithLifecycle()
    val showCreateDouListDialog by viewModel.showCreateDouListDialog.collectAsStateWithLifecycle()

    SubjectScreen(
        uiState = uiState,
        collectDialogUiState = collectDialogUiState,
        showCreateDouListDialog = showCreateDouListDialog,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::updateStatus,
        onImageClick = onImageClick,
        onUserClick = onUserClick,
        onRecommendSubjectClick = rememberRecommendSubjectClickHandler(
            onSubjectClick = onSubjectClick
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
fun SubjectScreen(
    uiState: SubjectUiState,
    collectDialogUiState: CollectDialogUiState?,
    showCreateDouListDialog: Boolean,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus, rating: Int?) -> Unit,
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
            if (uiState is SubjectUiState.Success) {
                SubjectReviewsSheetContent(
                    subjectType = uiState.subject.type,
                    reviews = uiState.reviews,
                    onUserClick = onUserClick
                )
            }
        },
        topBar = {
            SubjectTopBar(
                subjectType = (uiState as? SubjectUiState.Success)?.subject?.type
                    ?: SubjectType.UNSUPPORTED,
                subject = (uiState as? SubjectUiState.Success)?.subject,
                isLoggedIn = (uiState as? SubjectUiState.Success)?.isLoggedIn == true,
                onBackClick = onBackClick,
                onCollectClick = onCollectClick
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is SubjectUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    with(uiState) {
                        item {
                            SubjectDetailHeader(
                                subject = subject,
                                isLoggedIn = isLoggedIn,
                                onLoginClick = onLoginClick,
                                onUpdateStatus = onUpdateStatus,
                                onImageClick = onImageClick
                            )
                        }
                        subjectInfoIntroModuleItem(intro = subject.intro)

                        
                        when (subject) {
                            is MovieDetail -> {
                                creditList?.let { subjectInfoCelebritiesModuleItem(it) }
                                photos?.let {
                                    subjectInfoTrailersModuleItem(
                                        trailers = subject.trailers,
                                        photoList = it,
                                        onImageClick = onImageClick,
                                        onTrailerClick = { trailer ->
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(trailer.videoUrl.toUri(), "video/*")
                                            }
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }

                            is TvDetail -> {
                                creditList?.let { subjectInfoCelebritiesModuleItem(it) }
                                photos?.let {
                                    subjectInfoTrailersModuleItem(
                                        trailers = subject.trailers,
                                        photoList = it,
                                        onImageClick = onImageClick,
                                        onTrailerClick = { trailer ->
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(trailer.videoUrl.toUri(), "video/*")
                                            }
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }

                            is BookDetail -> {
                                
                            }
                        }

                        subjectInfoInterestsModuleItem(
                            interestList = interests,
                            onUserClick = onUserClick,
                            sortType = interestSortType,
                            onSortChange = onToggleInterestSort
                        )
                        subjectInfoRecommendModuleItem(
                            subjectType = subject.type,
                            recommendations = recommendations,
                            onRecommendSubjectClick = onRecommendSubjectClick
                        )
                    }
                }
            }

            is SubjectUiState.Loading -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            is SubjectUiState.Error -> {
                FullScreenErrorWithRetry(
                    message = uiState.message.getString(),
                    onRetryClick = onRetryClick,
                    contentPadding = innerPadding
                )
            }
        }
    }
}