package com.github.bumblebee202111.doubean.feature.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.FragmentNotificationsBinding
import com.github.bumblebee202111.doubean.feature.groups.common.TopicItemWithGroupAndroidView
import com.github.bumblebee202111.doubean.util.DEEP_LINK_SCHEME_AND_HOST
import com.github.bumblebee202111.doubean.util.GROUP_PATH
import com.github.bumblebee202111.doubean.util.TOPIC_PATH
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private lateinit var binding: FragmentNotificationsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_settings -> {
                        findNavController().navigate(R.id.nav_settings)
                        true
                    }

                    else -> false
                }
            }
            notifications.setContent {
                val notificationPagingItems =
                    notificationsViewModel.notifications.collectAsLazyPagingItems()
                val navigateToTopic = { topicId: String ->
                    val request =
                        NavDeepLinkRequest.Builder.fromUri(topicId.topicDeepLinkUri()).build()
                    findNavController().navigate(request)
                }
                LazyColumn(Modifier.nestedScroll(rememberNestedScrollInteropConnection())) {
                    items(
                        notificationPagingItems.itemCount,
                        notificationPagingItems.itemKey { it.id },
                        notificationPagingItems.itemContentType { "notification" }) { index ->
                        TopicItemWithGroupAndroidView(
                            notificationPagingItems[index],
                            navigateToTopic
                        )
                    }
                }
            }
        }
    }
}

fun String.topicDeepLinkUri() =
    "$DEEP_LINK_SCHEME_AND_HOST/$GROUP_PATH/$TOPIC_PATH/$this".toUri()