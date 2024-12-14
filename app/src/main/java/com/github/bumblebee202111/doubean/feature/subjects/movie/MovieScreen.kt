package com.github.bumblebee202111.doubean.feature.subjects.movie

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
import com.github.bumblebee202111.doubean.ui.subjectInfoCelebritiesModuleItem
import com.github.bumblebee202111.doubean.ui.subjectInfoInterestsModuleItem
import com.github.bumblebee202111.doubean.ui.subjectInfoIntroModuleItem
import com.github.bumblebee202111.doubean.ui.subjectInfoTrailersModuleItem

@Composable
fun MovieScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: MovieViewModel = hiltViewModel(),
) {
    val movieUiState by viewModel.movieUiState.collectAsStateWithLifecycle()
    MovieScreen(
        movieUiState = movieUiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(
    movieUiState: MovieUiState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
) {
    BottomSheetScaffold(
        sheetContent = {
            if (movieUiState is MovieUiState.Success) {
                SubjectInfoReviewsModuleItemContent(
                    subjectType = SubjectType.MOVIE,
                    reviews = movieUiState.reviews,
                    modifier = Modifier.fillMaxHeight(0.618f)
                )

            }
        },
        sheetPeekHeight = 128.dp,
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
                                onUpdateStatus = onUpdateStatus
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
                            photoList = photos
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
