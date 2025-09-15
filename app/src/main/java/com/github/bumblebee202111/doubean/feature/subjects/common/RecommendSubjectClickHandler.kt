package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun rememberRecommendSubjectClickHandler(
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
): (RecommendSubject) -> Unit {
    val context = LocalContext.current
    return remember(context, onMovieClick, onTvClick, onBookClick) {
        { recommendSubject ->
            when (recommendSubject.type) {
                SubjectType.MOVIE -> onMovieClick(recommendSubject.id)
                SubjectType.TV -> onTvClick(recommendSubject.id)
                SubjectType.BOOK -> onBookClick(recommendSubject.id)
                else -> {
                    OpenInUtils.openInDouban(context, recommendSubject.uri)
                }
            }
        }
    }
}