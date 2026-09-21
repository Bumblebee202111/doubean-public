package com.github.bumblebee202111.doubean.feature.subjects.detail.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.detail.SubjectScreen
import com.github.bumblebee202111.doubean.feature.subjects.detail.SubjectViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import com.github.bumblebee202111.doubean.shared.subject.model.SubjectType
import kotlinx.serialization.Serializable

@Serializable
data class SubjectDetailNavKey(val id: String, val type: SubjectType) : NavKey

fun EntryProviderScope<NavKey>.subjectDetailEntry(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
) {
    entry<SubjectDetailNavKey> { key ->
        SubjectScreen(
            onBackClick = onBackClick,
            onLoginClick = onLoginClick,
            onImageClick = onImageClick,
            onUserClick = onUserClick,
            onSubjectClick = onSubjectClick,
            viewModel = hiltViewModel<SubjectViewModel, SubjectViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.id, key.type)
                }
            )
        )
    }
}

fun Navigator.navigateToSubjectDetail(id: String, type: SubjectType) =
    navigate(SubjectDetailNavKey(id, type))