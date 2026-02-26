package com.github.bumblebee202111.doubean.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.GroupsHomeRoute
import com.github.bumblebee202111.doubean.feature.statuses.navigation.StatusesRoute
import com.github.bumblebee202111.doubean.feature.subjects.navigation.SubjectsRoute
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.UserProfileRoute

enum class TopLevelDestination(
    val route: Any,
    @StringRes val labelResId: Int,
    val iconVector: ImageVector,
) {
    Home(
        route = StatusesRoute,
        labelResId = R.string.title_statuses,
        iconVector = Icons.Filled.Timeline
    ),
    Subjects(
        route = SubjectsRoute,
        labelResId = R.string.title_subjects,
        iconVector = Icons.Filled.Collections
    ),
    Groups(
        route = GroupsHomeRoute,
        labelResId = R.string.title_groups,
        iconVector = Icons.Filled.Groups
    ),
    Me(
        route = UserProfileRoute(),
        labelResId = R.string.title_me,
        iconVector = Icons.Filled.Person
    )
}