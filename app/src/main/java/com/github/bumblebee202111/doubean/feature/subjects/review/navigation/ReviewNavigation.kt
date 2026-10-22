package com.github.bumblebee202111.doubean.feature.subjects.review.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.review.ReviewScreen
import com.github.bumblebee202111.doubean.feature.subjects.review.ReviewViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class ReviewNavKey(
    val reviewId: String,
) : NavKey

fun Navigator.navigateToReview(reviewId: String) =
    navigate(ReviewNavKey(reviewId))

fun EntryProviderScope<NavKey>.reviewEntry(
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Boolean,
) {
    entry<ReviewNavKey> { key ->
        ReviewScreen(
            onBackClick = onBackClick,
            onWebViewClick = onWebViewClick,
            onImageClick = onImageClick,
            onOpenDeepLinkUrl = onOpenDeepLinkUrl,
            viewModel = hiltViewModel<ReviewViewModel, ReviewViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.reviewId)
                }
            )
        )
    }
}