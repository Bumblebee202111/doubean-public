package com.github.bumblebee202111.doubean.feature.subjects.tv

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.ui.SubjectDetailHeader
import com.github.bumblebee202111.doubean.ui.SubjectReviewsSheetContent
import com.github.bumblebee202111.doubean.ui.SubjectScaffold
import com.github.bumblebee202111.doubean.ui.SubjectTopBar
import com.github.bumblebee202111.doubean.ui.subjectInfoCelebritiesModuleItem
import com.github.bumblebee202111.doubean.ui.subjectInfoInterestsModuleItem
import com.github.bumblebee202111.doubean.ui.subjectInfoIntroModuleItem
import com.github.bumblebee202111.doubean.ui.subjectInfoTrailersModuleItem

@Composable
fun TvScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: TvViewModel = hiltViewModel(),
) {
    val tvUiState by viewModel.tvUiState.collectAsStateWithLifecycle()
    TvScreen(
        tvUiState = tvUiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus
    )
}

@Composable
fun TvScreen(
    tvUiState: TvUiState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
) {
    SubjectScaffold(
        reviewsSheetContent = {
            if (tvUiState is TvUiState.Success) {
                SubjectReviewsSheetContent(
                    subjectType = SubjectType.TV,
                    reviews = tvUiState.reviews,
                )

            }
        },
        topBar = {
            TvTopBar(tvUiState = tvUiState, onBackClick = onBackClick)
        }
    ) { innerPadding ->
        when (tvUiState) {
            is TvUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    with(tvUiState) {
                        item {
                            SubjectDetailHeader(
                                subject = tv,
                                isLoggedIn = tvUiState.isLoggedIn,
                                onLoginClick = onLoginClick,
                                onUpdateStatus = onUpdateStatus
                            )
                        }
                        subjectInfoIntroModuleItem(intro = tv.intro)
                        subjectInfoInterestsModuleItem(interestList = interests)
                        subjectInfoCelebritiesModuleItem(
                            directorNames = tv.directorNames,
                            actorNames = tv.actorNames
                        )
                        subjectInfoTrailersModuleItem(trailers = tv.trailers, photoList = photos)
                    }
                }
            }

            else -> {

            }
        }
    }
}

@Composable
private fun TvTopBar(tvUiState: TvUiState, onBackClick: () -> Unit) {
    SubjectTopBar(
        subjectType = SubjectType.TV,
        subject = (tvUiState as? TvUiState.Success)?.tv,
        onBackClick = onBackClick
    )
}
