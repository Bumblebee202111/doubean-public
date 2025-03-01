package com.github.bumblebee202111.doubean.feature.groups.groupTab

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.TopicItem
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.model.toItem
import com.github.bumblebee202111.doubean.ui.GroupNotificationPreferencesDialog
import com.github.bumblebee202111.doubean.ui.SortTopicsByDropDownMenu
import com.github.bumblebee202111.doubean.ui.TopicItem
import com.github.bumblebee202111.doubean.ui.TopicItemDisplayMode
import com.github.bumblebee202111.doubean.ui.common.rememberLazyListStatePagingWorkaround
import com.github.bumblebee202111.doubean.util.ShareUtil

@Composable
fun GroupTabScreen(
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
) {
    val topicPagingItems = viewModel.topicsPagingData.collectAsLazyPagingItems()
    val shouldDisplayFavoritedTab = viewModel.shouldDisplayFavoritedTab
    val shouldDisplayUnfavoritedTab = viewModel.shouldDisplayUnfavoritedTab
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    val defaultNotificationsPreference by viewModel.defaultNotificationPreferences.collectAsStateWithLifecycle()
    GroupTabScreen(
        tabId = tabId,
        topicPagingItems = topicPagingItems,
        shouldDisplayFavoritedGroup = shouldDisplayFavoritedTab,
        shouldDisplayUnfavoritedTab = shouldDisplayUnfavoritedTab,
        sortBy = sortBy,
        group = group,
        defaultNotificationsPreference = defaultNotificationsPreference,
        clearFavoritedTabState = viewModel::clearFavoritedTabState,
        clearUnfavoritedTabState = viewModel::clearUnfavoritedTabState,
        updateSortBy = viewModel::updateSortBy,
        removeFavorite = viewModel::removeFavorite,
        addFavorite = viewModel::addFavorite,
        saveNotificationsPreference = viewModel::saveNotificationPreferences,
        onTopicClick = onTopicClick,
        onShowSnackbar = onShowSnackbar
    )
}

@Composable
fun GroupTabScreen(
    tabId: String?,
    topicPagingItems: LazyPagingItems<TopicItem>,
    shouldDisplayFavoritedGroup: Boolean,
    shouldDisplayUnfavoritedTab: Boolean,
    sortBy: TopicSortBy?,
    group: GroupDetail?,
    defaultNotificationsPreference: GroupNotificationPreferences?,
    clearFavoritedTabState: () -> Unit,
    clearUnfavoritedTabState: () -> Unit,
    updateSortBy: (topicSortBy: TopicSortBy) -> Unit,
    removeFavorite: () -> Unit,
    addFavorite: () -> Unit,
    saveNotificationsPreference: (preferences: GroupNotificationPreferences) -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
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
        state = topicPagingItems.rememberLazyListStatePagingWorkaround()

    ) {

        tabActionsItem(
            tabId = tabId,
            group = group,
            sortBy = sortBy,
            onOpenAlertDialog = { openAlertDialog = true },
            onSortByClick = updateSortBy,
            removeFavorite = removeFavorite,
            addFavorite = addFavorite
        )

        topicItems(topicPagingItems = topicPagingItems, group = group, onTopicClick = onTopicClick)
    }

    if (openAlertDialog) {
        group?.tabs?.find { it.id == tabId }?.let { tab ->
            if (defaultNotificationsPreference != null) {
                GroupNotificationPreferencesDialog(
                    titleTextResId = R.string.tab_notification_preferences,
                    initialPreference = tab.notificationPreferences
                        ?: defaultNotificationsPreference,
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
    group: GroupDetail?,
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
            group?.findTab(tabId)?.let { tab ->
                
                Row {
                    
                    
                    
                    if (tab.isFavorite || tab.notificationPreferences?.notificationsEnabled == true) {
                        TabNotificationsButton(
                            notificationsEnabled = tab.notificationPreferences?.notificationsEnabled
                                ?: false
                        ) {
                            onOpenAlertDialog()
                        }
                    }
                    if (tab.isFavorite) {
                        IconButton(removeFavorite) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null)
                        }
                    } else {
                        IconButton(addFavorite) {
                            Icon(imageVector = Icons.Default.StarBorder, contentDescription = null)
                        }
                    }

                    var moreExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { moreExpanded = !moreExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = moreExpanded, onDismissRequest = {
                            moreExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(
                                        R.string.share
                                    )
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

                            }
                        )
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
            group = group?.toItem(),
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
    ) {
        Icon(
            imageVector = if (notificationsEnabled) {
                Icons.Filled.Notifications
            } else {
                Icons.Filled.NotificationAdd
            },
            contentDescription = null
        )
    }
}
