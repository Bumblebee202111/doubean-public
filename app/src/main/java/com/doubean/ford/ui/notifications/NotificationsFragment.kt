package com.doubean.ford.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.doubean.ford.R
import com.doubean.ford.databinding.FragmentNotificationsBinding
import com.doubean.ford.model.RecommendedPostNotificationItem
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeFragmentDirections
import com.doubean.ford.util.DEEP_LINK_SCHEME_AND_HOST
import com.doubean.ford.util.GROUP_PATH
import com.doubean.ford.util.InjectorUtils
import com.doubean.ford.util.POST_PATH

class NotificationsFragment : Fragment() {
    private val notificationsViewModel: NotificationsViewModel by viewModels {
        InjectorUtils.provideNotificationsViewModelFactory(
            requireContext()
        )
    }
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
                    findNavController().navigate(R.id.navigation_settings)
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
        notificationsViewModel.notifications.observe(viewLifecycleOwner) {
            notificationAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }
}

private fun RecommendedPostNotificationItem.postDeepLinkUri() =
    "$DEEP_LINK_SCHEME_AND_HOST/$GROUP_PATH/$POST_PATH/$id".toUri()