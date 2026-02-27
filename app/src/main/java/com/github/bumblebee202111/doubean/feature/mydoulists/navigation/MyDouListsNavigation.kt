package com.github.bumblebee202111.doubean.feature.mydoulists.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.mydoulists.MyDouListsScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
object MyDouListsNavKey : NavKey

fun Navigator.navigateToMyDouLists() {
    this.navigate(MyDouListsNavKey)
}

fun EntryProviderScope<NavKey>.myDouListsEntry(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onImageClick: (imageUrl: String) -> Unit,
    onDouListClick: (douListId: String) -> Unit,
) {
    entry<MyDouListsNavKey> {
        MyDouListsScreen(
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            onBookClick = onBookClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onDouListClick = onDouListClick
        )
    }
}