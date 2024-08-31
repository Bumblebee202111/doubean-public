package com.github.bumblebee202111.doubean.feature.notifications

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.common.TopicItemWithGroupAndroidView
import com.github.bumblebee202111.doubean.model.TopicItemWithGroup
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.util.DEEP_LINK_SCHEME_AND_HOST
import com.github.bumblebee202111.doubean.util.GROUP_PATH
import com.github.bumblebee202111.doubean.util.TOPIC_PATH

@Composable
fun NotificationsScreen(
    onTopicClick: (topicId: String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val notificationPagingItems = viewModel.notifications.collectAsLazyPagingItems()
    NotificationsScreen(
        notificationPagingItems = notificationPagingItems,
        onTopicClick = onTopicClick,
        onSettingsClick = onSettingsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notificationPagingItems: LazyPagingItems<TopicItemWithGroup>,
    onTopicClick: (topicId: String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(topBar = {
        DoubeanTopAppBar(title = {}, actions = {
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
                Text(
                    text = stringResource(id = R.string.group_notifications_header),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(notificationPagingItems.itemCount,
                notificationPagingItems.itemKey { it.id },
                notificationPagingItems.itemContentType { "notification" }) { index ->
                TopicItemWithGroupAndroidView(
                    notificationPagingItems[index], onTopicClick
                )
            }
        }
    }
}


fun String.topicDeepLinkUri() = "$DEEP_LINK_SCHEME_AND_HOST/$GROUP_PATH/$TOPIC_PATH/$this".toUri()