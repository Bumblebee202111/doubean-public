package com.github.bumblebee202111.doubean.feature.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.FragmentNotificationsBinding
import com.github.bumblebee202111.doubean.feature.groups.groupsHome.GroupsHomeFragmentDirections
import com.github.bumblebee202111.doubean.model.RecommendedPostNotificationItem
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import com.github.bumblebee202111.doubean.util.DEEP_LINK_SCHEME_AND_HOST
import com.github.bumblebee202111.doubean.util.GROUP_PATH
import com.github.bumblebee202111.doubean.util.POST_PATH
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var notificationAdapter: NotificationAdapter
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

        binding.toolbar.setOnMenuItemClickListener { item ->
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

        notificationAdapter = NotificationAdapter { notification ->
            val request = NavDeepLinkRequest.Builder.fromUri(notification.postDeepLinkUri()).build()
            findNavController().navigate(request)
        }
        binding.notifications.adapter = notificationAdapter

        repeatWithViewLifecycle {
            notificationsViewModel.notifications.collect {
                notificationAdapter.submitData(it)
            }
        }
    }
}

private fun RecommendedPostNotificationItem.postDeepLinkUri() =
    "$DEEP_LINK_SCHEME_AND_HOST/$GROUP_PATH/$POST_PATH/$id".toUri()