package com.github.bumblebee202111.doubean.feature.groups.groupdetail.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.github.bumblebee202111.doubean.util.ShareUtil

@Composable
fun GroupTab(
    groupId: String,
    tabId: String?,
    group: GroupDetail?,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    viewModel: GroupTabViewModel = hiltViewModel<GroupTabViewModel, GroupTabViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(groupId, tabId)
        },
        key = groupId + tabId
    ),
    contentPadding: PaddingValues = PaddingValues(),
) {
    val topicPagingItems = viewModel.topicsPagingData.collectAsLazyPagingItems()
    val isFavorited by viewModel.isFavorited.collectAsStateWithLifecycle()
    val topicNotificationPreferences by viewModel.topicNotificationPreferences.collectAsStateWithLifecycle()
    val shouldDisplayFavoritedTab = viewModel.shouldDisplayFavoritedTab
    val shouldDisplayUnfavoritedTab = viewModel.shouldDisplayUnfavoritedTab
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    GroupTab(
        tabId = tabId,
        isFavorited = isFavorited,
        topicNotificationPreferences = topicNotificationPreferences,
        topicPagingItems = topicPagingItems,
        shouldDisplayFavoritedGroup = shouldDisplayFavoritedTab,
        shouldDisplayUnfavoritedTab = shouldDisplayUnfavoritedTab,
        sortBy = sortBy,
        group = group,
        clearFavoritedTabState = viewModel::clearFavoritedTabState,
        clearUnfavoritedTabState = viewModel::clearUnfavoritedTabState,
        updateSortBy = viewModel::updateSortBy,
        removeFavorite = viewModel::removeFavorite,
        addFavorite = viewModel::addFavorite,
        saveNotificationsPreference = viewModel::saveNotificationPreferences,
        onTopicClick = onTopicClick,
        onShowSnackbar = onShowSnackbar,
        contentPadding = contentPadding
    )
}

@Composable
fun GroupTab(
    tabId: String?,
    isFavorited: Boolean?,
    topicNotificationPreferences: GroupNotificationPreferences?,
    topicPagingItems: LazyPagingItems<TopicItem>,
    shouldDisplayFavoritedGroup: Boolean,
    shouldDisplayUnfavoritedTab: Boolean,
    sortBy: TopicSortBy?,
    group: GroupDetail?,
    clearFavoritedTabState: () -> Unit,
    clearUnfavoritedTabState: () -> Unit,
    updateSortBy: (topicSortBy: TopicSortBy) -> Unit,
    removeFavorite: () -> Unit,
    addFavorite: () -> Unit,
    saveNotificationsPreference: (preferences: GroupNotificationPreferences) -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {

    var openAlertDialog by remember { mutableStateOf(false) }

    val favoritedTabMessage = stringResource(id = R.string.favorited_tab)

    LaunchedEffect(key1 = shouldDisplayFavoritedGroup) {
        if (shouldDisplayFavoritedGroup) {
            onShowSnackbar(favoritedTabMessage)
            clearFavoritedTabState()
        }
    }

    val unfavoritedTabMessage = stringResource(id = R.string.unfavorited_tab)

    LaunchedEffect(key1 = shouldDisplayUnfavoritedTab) {
        if (shouldDisplayUnfavoritedTab) {
            onShowSnackbar(unfavoritedTabMessage)
            clearUnfavoritedTabState()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = contentPadding
    ) {

        if (group != null) {
            tabActionsItem(
                tabId = tabId,
                isFavorited = isFavorited,
                topicNotificationPreferences = topicNotificationPreferences,
                group = group,
                sortBy = sortBy,
                onOpenAlertDialog = { openAlertDialog = true },
                onSortByClick = updateSortBy,
                removeFavorite = removeFavorite,
                addFavorite = addFavorite
            )
        }

        topicItems(topicPagingItems = topicPagingItems, group = group, onTopicClick = onTopicClick)
    }

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

private fun LazyListScope.tabActionsItem(
    tabId: String?,
    isFavorited: Boolean?,
    topicNotificationPreferences: GroupNotificationPreferences?,
    group: GroupDetail,
    sortBy: TopicSortBy?,
    onOpenAlertDialog: () -> Unit,
    onSortByClick: (topicSortBy: TopicSortBy) -> Unit,
    removeFavorite: () -> Unit,
    addFavorite: () -> Unit,
) {

    item(
        key = "tab_actions", contentType = "tab_actions"
    ) {
        val context = LocalContext.current

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortTopicsByDropDownMenu(
                initialSortBy = sortBy ?: TopicSortBy.NEW_LAST_CREATED,
                onSortBySelected = onSortByClick
            )
            Spacer(Modifier.weight(1f))
            val tab = group.tabs.firstOrNull { it.id == tabId }
            if (tab != null && isFavorited != null && topicNotificationPreferences != null) {
                group.findTab(tabId)?.let { tab ->
                    //actions
                    Row {
                        // Only displayed when either condition meets:
                        // 1. Favorited
                        // 2. Record exists and enabled
                        if (isFavorited || topicNotificationPreferences.notificationsEnabled == true) {
                            TabNotificationsButton(
                                notificationsEnabled = topicNotificationPreferences.notificationsEnabled == true,
                                onOpenPreferencesDialog = onOpenAlertDialog
                            )
                        }
                        IconButton(
                            onClick = if (isFavorited) removeFavorite else addFavorite,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorited) Icons.Filled.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (isFavorited) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
                                        group.shareUrl?.let { shareUrl ->
                                            append(" $shareUrl\r\n")
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
    onTopicClick: (topicId: String) -> Unit,
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
            onTopicClick = onTopicClick
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
