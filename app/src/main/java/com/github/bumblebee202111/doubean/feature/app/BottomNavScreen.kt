package com.github.bumblebee202111.doubean.feature.app

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.GroupsHomeRoute
import com.github.bumblebee202111.doubean.feature.statuses.navigation.StatusesRoute
import com.github.bumblebee202111.doubean.feature.subjects.navigation.SubjectsRoute
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.UserProfileRoute
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.navigation.BottomNavHost
import com.github.bumblebee202111.doubean.navigation.navigateToBottomNavDestination
import kotlinx.coroutines.launch


enum class BottomNavDestination(
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

@Composable
fun BottomNavScreen(
    startWithGroups: Boolean,
    onActiveTabAppearanceNeeded: (useLightIcons: Boolean?) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToGroup: (groupId: String, tabId: String?) -> Unit,
    navigateToTopic: (topicId: String) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToSubjectInterests: (userId: String, subjectType: SubjectType) -> Unit,
    navigateToSearchSubjects: () -> Unit,
    navigateToRankList: (collectionId: String) -> Unit,
    navigateToMovie: (movieId: String) -> Unit,
    navigateToTv: (tvId: String) -> Unit,
    navigateToBook: (bookId: String) -> Unit,
    navigateToUserProfile: (userId: String) -> Unit,
    navigateToMyDouLists: () -> Unit,
    navigateToUri: (String) -> Boolean,
    viewModel: BottomNavViewModel = hiltViewModel(),
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    BottomNavScreen(
        startWithGroups = startWithGroups,
        currentUser = currentUser,
        onActiveTabAppearanceNeeded = onActiveTabAppearanceNeeded,
        navigateToSearch = navigateToSearch,
        navigateToNotifications = navigateToNotifications,
        navigateToSettings = navigateToSettings,
        navigateToGroupDetail = navigateToGroup,
        navigateToTopic = navigateToTopic,
        navigateToLogin = navigateToLogin,
        navigateToSubjectInterests = navigateToSubjectInterests,
        navigateToSearchSubjects = navigateToSearchSubjects,
        navigateToRankList = navigateToRankList,
        navigateToMovie = navigateToMovie,
        navigateToTv = navigateToTv,
        navigateToBook = navigateToBook,
        navigateToUserProfile = navigateToUserProfile,
        navigateToMyDouLists = navigateToMyDouLists,
        navigateToUri = navigateToUri
    )
}

@Composable
fun BottomNavScreen(
    startWithGroups: Boolean,
    currentUser: User?,
    onActiveTabAppearanceNeeded: (useLightIcons: Boolean?) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToGroupDetail: (groupId: String, tabId: String?) -> Unit,
    navigateToTopic: (topicId: String) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToSubjectInterests: (userId: String, subjectType: SubjectType) -> Unit,
    navigateToSearchSubjects: () -> Unit,
    navigateToRankList: (collectionId: String) -> Unit,
    navigateToMovie: (movieId: String) -> Unit,
    navigateToTv: (tvId: String) -> Unit,
    navigateToBook: (bookId: String) -> Unit,
    navigateToUserProfile: (userId: String) -> Unit,
    navigateToMyDouLists: () -> Unit,
    navigateToUri: (String) -> Boolean,
) {
    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            NavigationDrawerSheet(
                currentUser = currentUser,
                onHeaderClick = {
                    scope.launch { drawerState.close() }
                    navigateToUserProfile(currentUser?.uid ?: return@NavigationDrawerSheet)
                },
                onCollectClick = {
                    scope.launch { drawerState.close() }
                    navigateToMyDouLists()
                },
                onSettingsClick = {
                    scope.launch { drawerState.close() }
                    navigateToSettings()
                }
            )
        },
        drawerState = drawerState
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val useLightIcons: Boolean? = remember(currentDestination) {
            if (currentDestination?.hasRoute(UserProfileRoute::class) == true) {
                true
            } else {
                null
            }
        }
        LaunchedEffect(useLightIcons) {
            onActiveTabAppearanceNeeded(useLightIcons)
        }
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    BottomNavDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            icon = { Icon(destination.iconVector, contentDescription = null) },
                            label = { Text(stringResource(destination.labelResId)) },
                            selected = currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true,
                            onClick = {
                                navController.navigateToBottomNavDestination(destination)
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            BottomNavHost(
                navController = navController,
                startDestination = if (startWithGroups) GroupsHomeRoute else SubjectsRoute,
                navigateToSearch = navigateToSearch,
                navigateToNotifications = navigateToNotifications,
                navigateToSettings = navigateToSettings,
                navigateToGroupDetail = navigateToGroupDetail,
                navigateToTopic = navigateToTopic,
                navigateToLogin = navigateToLogin,
                navigateToSubjectInterests = navigateToSubjectInterests,
                navigateToSearchSubjects = navigateToSearchSubjects,
                navigateToRankList = navigateToRankList,
                navigateToMovie = navigateToMovie,
                navigateToTv = navigateToTv,
                navigateToBook = navigateToBook,
                navigateToUri = navigateToUri,
                onAvatarClick = {
                    scope.launch { drawerState.open() }
                },
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            )
        }
    }

}

@Composable
private fun NavigationDrawerSheet(
    currentUser: User?,
    onHeaderClick: () -> Unit,
    onCollectClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            UserDrawerHeader(
                currentUser = currentUser,
                onHeaderClick = onHeaderClick
            )
            HorizontalDivider()

            Spacer(Modifier.height(12.dp))

            NavigationDrawerItem(
                label = { Text(stringResource(id = R.string.title_slide_menu_collect)) },
                selected = false,
                onClick = onCollectClick,
                icon = { Icon(Icons.Default.Collections, contentDescription = null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.settings)) },
                selected = false,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = onSettingsClick,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}