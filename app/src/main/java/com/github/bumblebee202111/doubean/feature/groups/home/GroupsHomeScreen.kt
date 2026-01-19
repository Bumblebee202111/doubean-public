package com.github.bumblebee202111.doubean.feature.groups.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.model.groups.PinnedTabItem
import com.github.bumblebee202111.doubean.ui.common.AppBarNavigationAvatar
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar

@Composable
fun GroupsHomeScreen(
    viewModel: GroupsHomeViewModel = hiltViewModel(),
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onTopicClick: (topicId: String) -> Unit,
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val joinedGroupsUiState by viewModel.joinedGroupsUiState.collectAsStateWithLifecycle()
    val pinnedTabs by viewModel.pinnedTabs.collectAsStateWithLifecycle()
    val dayRankingUiState by viewModel.dayRankingUiState.collectAsStateWithLifecycle()
    val recentTopicsFeed by viewModel.recentTopicsFeedUiState.collectAsStateWithLifecycle()
    GroupsHomeScreen(
        currentUser = currentUser,
        joinedGroupsUiState = joinedGroupsUiState,
        pinnedTabs = pinnedTabs,
        dayRankingUiState = dayRankingUiState,
        recentTopicsFeedUiState = recentTopicsFeed,
        onAvatarClick = onAvatarClick,
        onSearchClick = onSearchClick,
        onNotificationsClick = onNotificationsClick,
        onGroupClick = onGroupClick,
        onTopicClick = onTopicClick,
        onRetryJoinedGroups = viewModel::retryJoinedGroups,
        onRetryRecentTopicsFeed = viewModel::retryRecentTopicsFeed,
        onRetryDayRanking = viewModel::retryDayRanking,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GroupsHomeScreen(
    currentUser: User?,
    joinedGroupsUiState: JoinedGroupsUiState,
    pinnedTabs: List<PinnedTabItem>?,
    dayRankingUiState: DayRankingUiState,
    recentTopicsFeedUiState: RecentTopicsFeedUiState,
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onGroupClick: (String, String?) -> Unit,
    onTopicClick: (String) -> Unit,
    onRetryJoinedGroups: () -> Unit,
    onRetryRecentTopicsFeed: () -> Unit,
    onRetryDayRanking: () -> Unit,
) {
    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                navigationIcon = {
                    AppBarNavigationAvatar(
                        currentUser = currentUser,
                        onAvatarClick = onAvatarClick
                    )
                },
                title = {},
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(
                    LayoutDirection.Ltr
                ),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {

            myGroups(
                uiState = joinedGroupsUiState,
                onGroupItemClick = { onGroupClick(it, null) },
                onRetryClick = onRetryJoinedGroups
            )

            itemSpacer()

            pinnedTabs?.takeIf { it.isNotEmpty() }?.let { pinnedTabs ->
                pinnedTabs(pinnedTabs = pinnedTabs, onItemClick = onGroupClick)
                itemSpacer()
            }

            if (dayRankingUiState !is DayRankingUiState.Hidden) {
                dayRanking(
                    uiState = dayRankingUiState,
                    onGroupItemClick = { onGroupClick(it, null) },
                    onRetryClick = onRetryDayRanking
                )
            }

            if (recentTopicsFeedUiState.topics != null || recentTopicsFeedUiState.isLoading || recentTopicsFeedUiState.errorMessage != null) {
                myTopics(
                    uiState = recentTopicsFeedUiState,
                    onTopicClick = onTopicClick,
                    onGroupClick = { groupId -> onGroupClick(groupId, null) },
                    onRetryClick = onRetryRecentTopicsFeed
                )
            }
        }
    }
}

