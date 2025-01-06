package com.github.bumblebee202111.doubean.feature.groups.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.groups.search.GroupsSearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object GroupsSearchRoute

fun NavController.navigateToSearch() = navigate(route = GroupsSearchRoute)

fun NavGraphBuilder.groupsSearchScreen(onGroupClick: (String) -> Unit, onBackClick: () -> Unit) =
    composable<GroupsSearchRoute> {
        GroupsSearchScreen(onGroupClick = onGroupClick, onBackClick = onBackClick)
    }