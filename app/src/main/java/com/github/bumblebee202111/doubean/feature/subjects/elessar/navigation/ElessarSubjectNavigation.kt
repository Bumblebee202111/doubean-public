package com.github.bumblebee202111.doubean.feature.subjects.elessar.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.elessar.ElessarSubjectScreen
import com.github.bumblebee202111.doubean.feature.subjects.elessar.ElessarSubjectViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class ElessarSubjectNavKey(val subjectId: String) : NavKey

fun Navigator.navigateToElessarSubject(subjectId: String) =
    navigate(ElessarSubjectNavKey(subjectId))

fun EntryProviderScope<NavKey>.elessarSubjectEntry(
    onBackClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (uri: String) -> Boolean,
) {
    entry<ElessarSubjectNavKey> { key ->
        ElessarSubjectScreen(
            onBackClick = onBackClick,
            onImageClick = onImageClick,
            onOpenDeepLinkUrl = onOpenDeepLinkUrl,
            viewModel = hiltViewModel<ElessarSubjectViewModel, ElessarSubjectViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.subjectId)
                }
            )
        )
    }
}