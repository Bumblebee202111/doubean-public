package com.github.bumblebee202111.doubean.feature.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.common.TopicItemWithGroupAndroidView
import com.github.bumblebee202111.doubean.ui.theme.AppTheme
import com.github.bumblebee202111.doubean.util.DEEP_LINK_SCHEME_AND_HOST
import com.github.bumblebee202111.doubean.util.GROUP_PATH
import com.github.bumblebee202111.doubean.util.TOPIC_PATH
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?,
    ) = content {
        AppTheme {
            NotificationsScreen(
                viewModel = viewModel(),
                navigateToTopic = { topicId ->
                    val request =
                        NavDeepLinkRequest.Builder.fromUri(topicId.topicDeepLinkUri()).build()
                    findNavController().navigate(request)
                },
                openSettings = {
                    findNavController().navigate(R.id.nav_settings)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    navigateToTopic: (topicId: String) -> Unit,
    openSettings: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(title = {}, actions = {
            IconButton(onClick = openSettings) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null
                )
            }
        })
    }) { innerPadding ->
        val notificationPagingItems = viewModel.notifications.collectAsLazyPagingItems()

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
                    notificationPagingItems[index], navigateToTopic
                )
            }
        }
    }
}


fun String.topicDeepLinkUri() = "$DEEP_LINK_SCHEME_AND_HOST/$GROUP_PATH/$TOPIC_PATH/$this".toUri()