package com.github.bumblebee202111.doubean.feature.subjects.detail

import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoBookSeriesModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoBookVersionsModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoCelebritiesModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoInterestsModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoIntroModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoRecommendModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoSongsModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.common.subjectInfoTrailersModuleItem
import com.github.bumblebee202111.doubean.model.doulist.ItemDouList
import com.github.bumblebee202111.doubean.model.subject.BookDetail
import com.github.bumblebee202111.doubean.model.subject.MovieDetail
import com.github.bumblebee202111.doubean.model.subject.MusicDetail
import com.github.bumblebee202111.doubean.model.subject.RecommendSubject
import com.github.bumblebee202111.doubean.model.subject.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subject.SubjectType
import com.github.bumblebee202111.doubean.model.subject.TvDetail
import com.github.bumblebee202111.doubean.ui.common.CollectDialogUiState
import com.github.bumblebee202111.doubean.ui.common.CreateDouListDialog
import com.github.bumblebee202111.doubean.ui.common.DouListDialog
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun SubjectScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
    onCelebrityClick: (celebrityId: String) -> Unit,
    onReviewClick: (reviewId: String) -> Unit,
    onOpenDeepLinkUrl: (uri: String) -> Boolean = { false },
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
        onSubjectClick = onSubjectClick,
        onCelebrityClick = onCelebrityClick,
        onReviewClick = onReviewClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
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

@OptIn(ExperimentalMaterial3Api::class)
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
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
    onCelebrityClick: (celebrityId: String) -> Unit,
    onReviewClick: (reviewId: String) -> Unit,
    onOpenDeepLinkUrl: (uri: String) -> Boolean = { false },
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
    var showSubjectInfoSheet by rememberSaveable { mutableStateOf(false) }

    if (showSubjectInfoSheet && uiState is SubjectUiState.Success) {
        ModalBottomSheet(
            onDismissRequest = { showSubjectInfoSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            SubjectInfoContentWebView(
                subjectType = uiState.subject.type,
                subjectId = uiState.subject.id,
                onOpenDeepLinkUrl = { uri ->
                    val handled = onOpenDeepLinkUrl(uri)
                    if (handled) {
                        showSubjectInfoSheet = false
                    }
                    handled
                }
            )
        }
    }

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
                    onReviewClick = onReviewClick,
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
                                onImageClick = onImageClick,
                                onMetaInfoClick = { showSubjectInfoSheet = true }
                            )
                        }
                        subjectInfoIntroModuleItem(intro = subject.intro)

                        when (subject) {
                            is MovieDetail -> {
                                creditList?.let {
                                    subjectInfoCelebritiesModuleItem(
                                        creditList = it,
                                        onCelebrityClick = onCelebrityClick
                                    )
                                }
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
                                creditList?.let {
                                    subjectInfoCelebritiesModuleItem(
                                        creditList = it,
                                        onCelebrityClick = onCelebrityClick
                                    )
                                }
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

                            is BookDetail -> Unit

                            is MusicDetail -> {
                                subjectInfoSongsModuleItem(subject.tracks)
                            }
                        }

                        subjectInfoInterestsModuleItem(
                            interestList = interests,
                            onUserClick = onUserClick,
                            sortType = interestSortType,
                            onSortChange = onToggleInterestSort
                        )

                        if (subject is BookDetail) {
                            subject.bookSeries?.let { series ->
                                subjectInfoBookSeriesModuleItem(
                                    series = series,
                                    onClick = {
                                        OpenInUtils.openInDouban(context, series.uri).onFailure {
                                            OpenInUtils.openInBrowser(context, series.url)
                                        }
                                    }
                                )
                            }

                            if (bookVersions != null && bookVersions.versions.isNotEmpty()) {
                                subjectInfoBookVersionsModuleItem(
                                    versions = bookVersions,
                                    onBookClick = { versionId ->
                                        onSubjectClick(
                                            versionId,
                                            SubjectType.BOOK
                                        )
                                    }
                                )
                            }
                        }

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