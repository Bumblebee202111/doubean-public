package com.github.bumblebee202111.doubean.feature.groups.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.groups.search.GroupsSearchScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object GroupsSearchNavKey : NavKey

fun Navigator.navigateToSearch() = navigate(key = GroupsSearchNavKey)

fun EntryProviderScope<NavKey>.groupsSearchEntry(
    onGroupClick: (String) -> Unit,
    onBackClick: () -> Unit,
) =
    entry<GroupsSearchNavKey> {
        GroupsSearchScreen(
            onGroupClick = onGroupClick,
            onBackClick = onBackClick
        )
    }