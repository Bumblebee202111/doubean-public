package com.github.bumblebee202111.doubean.feature.groups.groupdetail.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.GroupNotificationPreferencesDialog
import com.github.bumblebee202111.doubean.feature.groups.shared.SortTopicsByDropDownMenu
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicItem
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicItemDisplayMode
import com.github.bumblebee202111.doubean.model.groups.GroupDetail
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.groups.TopicItem
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy
import com.github.bumblebee202111.doubean.model.groups.toSimpleGroup
import com.github.bumblebee202111.doubean.ui.component.FullScreenCenteredContent
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.InfoButton
import com.github.bumblebee202111.doubean.ui.component.InfoDialog
import com.github.bumblebee202111.doubean.ui.util.toUiMessage
import com.github.bumblebee202111.doubean.util.ShareUtil

@Composable
fun GroupTab(
    groupId: String,
    tabId: String?,
    group: GroupDetail?,
    isPullToRefreshEnabled: Boolean,
    onTopicClick: (uri: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    viewModel: GroupTabViewModel = hiltViewModel<GroupTabViewModel, GroupTabViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(groupId, tabId)
        },
        key = groupId + tabId
    ),
    contentPadding: PaddingValues = PaddingValues(),
) {
    val topicPagingItems = viewModel.topicsPagingData.collectAsLazyPagingItems()
    val isPinned by viewModel.isPinned.collectAsStateWithLifecycle()
    val topicNotificationPreferences by viewModel.topicNotificationPreferences.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    GroupTab(
        tabId = tabId,
        isPinned = isPinned,
        topicNotificationPreferences = topicNotificationPreferences,
        topicPagingItems = topicPagingItems,
        sortBy = sortBy,
        group = group,
        isPullToRefreshEnabled = isPullToRefreshEnabled,
        updateSortBy = viewModel::updateSortBy,
        unpinTab = viewModel::unpinTab,
        pinTab = viewModel::pinTab,
        saveNotificationsPreference = viewModel::saveNotificationPreferences,
        onTopicClick = onTopicClick,
        onUserClick = onUserClick,
        contentPadding = contentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTab(
    tabId: String?,
    isPinned: Boolean?,
    topicNotificationPreferences: GroupNotificationPreferences?,
    topicPagingItems: LazyPagingItems<TopicItem>,
    sortBy: TopicSortBy?,
    group: GroupDetail?,
    isPullToRefreshEnabled: Boolean,
    updateSortBy: (topicSortBy: TopicSortBy) -> Unit,
    unpinTab: () -> Unit,
    pinTab: () -> Unit,
    saveNotificationsPreference: (preferences: GroupNotificationPreferences) -> Unit,
    onTopicClick: (uri: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {

    var openAlertDialog by remember { mutableStateOf(false) }

    val isRefreshing = topicPagingItems.loadState.refresh is LoadState.Loading

    val pullToRefreshState = rememberPullToRefreshState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                isRefreshing = isRefreshing,
                state = pullToRefreshState,
                onRefresh = { topicPagingItems.refresh() },
                enabled = isPullToRefreshEnabled,
            )
    ) {
        when (val refreshState = topicPagingItems.loadState.refresh) {
            is LoadState.Error -> {
                FullScreenErrorWithRetry(
                    message = refreshState.toUiMessage().getString(),
                    onRetryClick = { topicPagingItems.retry() },
                    contentPadding = contentPadding
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = contentPadding
                ) {

                    if (group != null) {
                        tabActionsItem(
                            tabId = tabId,
                            isPinned = isPinned,
                            topicNotificationPreferences = topicNotificationPreferences,
                            group = group,
                            sortBy = sortBy,
                            onOpenAlertDialog = { openAlertDialog = true },
                            onSortByClick = updateSortBy,
                            unpinTab = unpinTab,
                            pinTab = pinTab
                        )
                    }

                    if (topicPagingItems.itemCount == 0 && !isRefreshing) {
                        item {
                            FullScreenCenteredContent(contentPadding = PaddingValues(top = 128.dp)) {
                                Text(stringResource(R.string.empty_content_title))
                            }
                        }
                    } else {
                        topicItems(
                            topicPagingItems = topicPagingItems,
                            group = group,
                            onTopicClick = onTopicClick,
                            onUserClick = onUserClick
                        )
                    }

                    item {
                        when (val appendState = topicPagingItems.loadState.append) {
                            is LoadState.Loading -> {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is LoadState.Error -> {
                                Text(
                                    text = appendState.toUiMessage().getString(),
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .clickable { topicPagingItems.retry() },
                                    textAlign = TextAlign.Center
                                )
                            }

                            else -> Unit

                        }
                    }
                }
            }
        }

        PullToRefreshDefaults.Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = pullToRefreshState
        )

        if (openAlertDialog) {
            group?.tabs?.find { it.id == tabId }?.let { tab ->
                if (topicNotificationPreferences != null) {
                    GroupNotificationPreferencesDialog(
                        titleTextResId = R.string.tab_notification_preferences,
                        initialPreference = topicNotificationPreferences,
                        onDismissRequest = {
                            openAlertDialog = false
                        }) { preferencesToSave ->
                        saveNotificationsPreference(
                            preferencesToSave
                        )
                        openAlertDialog = false
                    }
                }
            }
        }
    }
}

private fun LazyListScope.tabActionsItem(
    tabId: String?,
    isPinned: Boolean?,
    topicNotificationPreferences: GroupNotificationPreferences?,
    group: GroupDetail,
    sortBy: TopicSortBy?,
    onOpenAlertDialog: () -> Unit,
    onSortByClick: (topicSortBy: TopicSortBy) -> Unit,
    unpinTab: () -> Unit,
    pinTab: () -> Unit,
) {

    item(
        key = "tab_actions", contentType = "tab_actions"
    ) {
        val context = LocalContext.current

        var showSortInfoDialog by remember { mutableStateOf(false) }

        if (showSortInfoDialog) {
            InfoDialog(
                onDismissRequest = { showSortInfoDialog = false },
                title = stringResource(R.string.sort_topics_info_title),
                text = stringResource(R.string.sort_topics_info_body)
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SortTopicsByDropDownMenu(
                    initialSortBy = sortBy ?: TopicSortBy.NEW_LAST_CREATED,
                    onSortBySelected = onSortByClick
                )
                InfoButton(
                    onClick = { showSortInfoDialog = true },
                    contentDescription = stringResource(R.string.sort_topics_info_title)
                )
            }
            Spacer(Modifier.weight(1f))
            val tab = group.tabs.firstOrNull { it.id == tabId }
            if (tab != null && isPinned != null && topicNotificationPreferences != null) {
                group.findTab(tabId)?.let { tab ->
                    
                    Row {
                        
                        
                        
                        if (isPinned || topicNotificationPreferences.notificationsEnabled) {
                            TabNotificationsButton(
                                notificationsEnabled = topicNotificationPreferences.notificationsEnabled,
                                onOpenPreferencesDialog = onOpenAlertDialog
                            )
                        }
                        IconButton(
                            onClick = if (isPinned) unpinTab else pinTab,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (isPinned) stringResource(R.string.unpin_tab) else stringResource(
                                    R.string.pin_tab
                                ),
                                tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        var moreExpanded by remember { mutableStateOf(false) }
                        IconButton(
                            onClick = { moreExpanded = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = moreExpanded, onDismissRequest = {
                                moreExpanded = false
                            },
                            modifier = Modifier.background(
                                MaterialTheme.colorScheme.surface
                            )
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(
                                            R.string.share
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    val shareText = buildString {
                                        append(group.name + "|")
                                        append(tab.name)
                                        group.sharingUrl?.let { sharingUrl ->
                                            append(" $sharingUrl\r\n")
                                        }
                                    }
                                    ShareUtil.share(context, shareText)

                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Share,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }


                }
            }

        }
    }
}

private fun LazyListScope.topicItems(
    topicPagingItems: LazyPagingItems<TopicItem>,
    group: GroupDetail?,
    onTopicClick: (uri: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
) {
    items(
        count = topicPagingItems.itemCount,
        key = topicPagingItems.itemKey { it.id },
        contentType = topicPagingItems.itemContentType { "topicItem" }
    ) { index ->
        val topic = topicPagingItems[index]
        TopicItem(
            topic = topic,
            group = group?.toSimpleGroup(),
            displayMode = TopicItemDisplayMode.SHOW_AUTHOR,
            onTopicClick = onTopicClick,
            onAuthorClick = onUserClick
        )
        if (index != topicPagingItems.itemCount - 1) {
            HorizontalDivider()
        }
    }
}

@Composable
private fun TabNotificationsButton(
    notificationsEnabled: Boolean,
    onOpenPreferencesDialog: () -> Unit,
) {
    IconButton(
        onClick = onOpenPreferencesDialog,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = if (notificationsEnabled) {
                Icons.Default.Notifications
            } else {
                Icons.Default.NotificationAdd
            },
            contentDescription = null,
            tint = if (notificationsEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
