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
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectType

@Composable
fun MovieScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    viewModel: MovieViewModel = hiltViewModel(),
) {
    val movieUiState by viewModel.movieUiState.collectAsStateWithLifecycle()
    MovieScreen(
        movieUiState = movieUiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus,
        onImageClick = onImageClick
    )
}

@Composable
fun MovieScreen(
    movieUiState: MovieUiState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
    onImageClick: (url: String) -> Unit,
) {
    SubjectScaffold(
        reviewsSheetContent = {
            if (movieUiState is MovieUiState.Success) {
                SubjectReviewsSheetContent(
                    subjectType = SubjectType.MOVIE,
                    reviews = movieUiState.reviews
                )
            }
        },
        topBar = {
            MovieTopBar(movieUiState = movieUiState, onBackClick = onBackClick)
        }
    ) { innerPadding ->

        when (movieUiState) {
            is MovieUiState.Success -> {
                LazyColumn(
                    contentPadding = innerPadding,
                ) {
                    with(movieUiState) {
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
    movieUiState: MovieUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SubjectTopBar(
        subjectType = SubjectType.MOVIE,
        subject = (movieUiState as? MovieUiState.Success)?.movie,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
