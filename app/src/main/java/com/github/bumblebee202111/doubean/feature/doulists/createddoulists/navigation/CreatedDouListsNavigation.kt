package com.github.bumblebee202111.doubean.feature.doulists.createddoulists.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.CreatedDouListsScreen
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.CreatedDouListsViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class CreatedDouListsNavKey(
    val userId: String,
) : NavKey

fun Navigator.navigateToCreatedDouLists(
    userId: String,
) {
    this.navigate(key = CreatedDouListsNavKey(userId = userId))
}

fun EntryProviderScope<NavKey>.createdDouListsEntry(
    onBackClick: () -> Unit,
    onDouListClick: (douListId: String) -> Unit,
) {
    entry<CreatedDouListsNavKey> { key ->
        CreatedDouListsScreen(
            onBackClick = onBackClick,
            onDouListClick = onDouListClick,
            viewModel = hiltViewModel<CreatedDouListsViewModel, CreatedDouListsViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.userId)
                }
            )
        )
    }
}