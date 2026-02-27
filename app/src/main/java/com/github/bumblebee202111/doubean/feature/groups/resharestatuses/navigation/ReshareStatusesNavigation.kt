package com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.ReshareStatusesScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class ReshareStatusesNavKey(
    val topicId: String,
) : NavKey

fun Navigator.navigateToReshareStatuses(topicId: String) = navigate(
    key = ReshareStatusesNavKey(topicId)
)

fun EntryProviderScope<NavKey>.reshareStatusesEntry(
    onBackClick: () -> Unit,
    onUserClick: (id: String) -> Unit,
) =
    entry<ReshareStatusesNavKey> {
        ReshareStatusesScreen(
            onBackClick = onBackClick,
            onUserClick = onUserClick
        )
    }