package com.github.bumblebee202111.doubean.feature.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.FragmentNotificationsBinding
import com.github.bumblebee202111.doubean.databinding.ListItemPostNotificationBinding
import com.github.bumblebee202111.doubean.feature.groups.groupsHome.GroupsHomeFragmentDirections
import com.github.bumblebee202111.doubean.model.RecommendedPostNotificationItem
import com.github.bumblebee202111.doubean.ui.common.UserProfileImage
import com.github.bumblebee202111.doubean.util.DEEP_LINK_SCHEME_AND_HOST
import com.github.bumblebee202111.doubean.util.GROUP_PATH
import com.github.bumblebee202111.doubean.util.POST_PATH
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
                    R.id.action_search -> {
                        val action =
                            GroupsHomeFragmentDirections.actionGroupsToGroupSearch()
                        findNavController().navigate(action)
                        true
                    }

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
                val navigateToTopic = { notification: RecommendedPostNotificationItem ->
                    val request =
                        NavDeepLinkRequest.Builder.fromUri(notification.postDeepLinkUri()).build()
                    findNavController().navigate(request)
                }
                LazyColumn(Modifier.nestedScroll(rememberNestedScrollInteropConnection())) {
                    items(
                        notificationPagingItems.itemCount,
                        notificationPagingItems.itemKey { it.id },
                        notificationPagingItems.itemContentType { "notification" }) { index ->
                        AndroidViewBinding(
                            factory = ListItemPostNotificationBinding::inflate,
                            onReset = {}) {
                            val notification = notificationPagingItems[index]
                            post = notification
                            //TODO Do not use Circle for group avatars
                            groupAvatar.setContent {
                                UserProfileImage(
                                    url = notification?.group?.avatarUrl,
                                    size = dimensionResource(id = R.dimen.icon_size_extra_small)
                                )
                            }
                            cover.setContent {
                                notification?.coverUrl?.let {
                                    AsyncImage(
                                        model = it, contentDescription = null,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_normal))),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                            }
                            card.setOnClickListener {
                                notification?.let(navigateToTopic)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun RecommendedPostNotificationItem.postDeepLinkUri() =
    "$DEEP_LINK_SCHEME_AND_HOST/$GROUP_PATH/$POST_PATH/$id".toUri()