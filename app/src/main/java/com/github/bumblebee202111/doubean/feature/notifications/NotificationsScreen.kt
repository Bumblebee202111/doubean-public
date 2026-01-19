package com.github.bumblebee202111.doubean.feature.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicItem
import com.github.bumblebee202111.doubean.feature.groups.shared.TopicItemDisplayMode
import com.github.bumblebee202111.doubean.model.groups.TopicItemWithGroup
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.InfoButton
import com.github.bumblebee202111.doubean.ui.component.InfoDialog
import com.github.bumblebee202111.doubean.util.DEEP_LINK_SCHEME_AND_HOST
import com.github.bumblebee202111.doubean.util.GROUP_PATH
import com.github.bumblebee202111.doubean.util.TOPIC_PATH

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onGroupClick: (groupId: String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val notificationPagingItems = viewModel.notifications.collectAsLazyPagingItems()
    NotificationsScreen(
        notificationPagingItems = notificationPagingItems,
        onTopicClick = onTopicClick,
        onGroupClick = onGroupClick,
        onBackClick = onBackClick,
        onSettingsClick = onSettingsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notificationPagingItems: LazyPagingItems<TopicItemWithGroup>,
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onGroupClick: (groupId: String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        InfoDialog(
            onDismissRequest = { showInfoDialog = false },
            title = stringResource(R.string.notifications_info_title),
            text = stringResource(R.string.notifications_info_body)
        )
    }
    Scaffold(topBar = {
        DoubeanTopAppBar(
            titleText = stringResource(R.string.title_notifications),
            navigationIcon = {
                BackButton(onClick = onBackClick)
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null
                    )
                }
            })
    }) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.group_notifications_header),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    InfoButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.size(24.dp),
                        contentDescription = stringResource(R.string.notifications_info_title)
                    )
                }
            }
            if (notificationPagingItems.itemCount == 0 &&
                notificationPagingItems.loadState.refresh is LoadState.NotLoading
            ) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.empty_notifications),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(
                    notificationPagingItems.itemCount,
                    notificationPagingItems.itemKey { it.id },
                    notificationPagingItems.itemContentType { "notification" }) { index ->
                    TopicItem(
                        topicItemWithGroup = notificationPagingItems[index],
                        displayMode = TopicItemDisplayMode.SHOW_GROUP,
                        onTopicClick = onTopicClick,
                        onAuthorClick = onGroupClick
                    )
                }
            }
        }
    }
}


fun String.topicDeepLinkUri() = "$DEEP_LINK_SCHEME_AND_HOST/$GROUP_PATH/$TOPIC_PATH/$this".toUri()