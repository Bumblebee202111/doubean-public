package com.github.bumblebee202111.doubean.feature.subjects.movie

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectDetailHeader
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectReviewsSheetContent
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectScaffold
import com.github.bumblebee202111.doubean.feature.subjects.shared.SubjectTopBar
import com.github.bumblebee202111.doubean.feature.subjects.shared.subjectInfoCelebritiesModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.shared.subjectInfoInterestsModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.shared.subjectInfoIntroModuleItem
import com.github.bumblebee202111.doubean.feature.subjects.shared.subjectInfoTrailersModuleItem
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

@Composable
fun MovieScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    viewModel: MovieViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MovieScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus,
        onImageClick = onImageClick
    )
}

@Composable
fun MovieScreen(
    uiState: MovieUiState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
    onImageClick: (url: String) -> Unit,
) {
    SubjectScaffold(
        reviewsSheetContent = {
            if (uiState is MovieUiState.Success) {
                SubjectReviewsSheetContent(
                    subjectType = SubjectType.MOVIE,
                    reviews = uiState.reviews
                )
            }
        },
        topBar = {
            MovieTopBar(uiState = uiState, onBackClick = onBackClick)
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
                        subjectInfoInterestsModuleItem(interestList = interests)
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
    uiState: MovieUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SubjectTopBar(
        subjectType = SubjectType.MOVIE,
        subject = (uiState as? MovieUiState.Success)?.movie,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
