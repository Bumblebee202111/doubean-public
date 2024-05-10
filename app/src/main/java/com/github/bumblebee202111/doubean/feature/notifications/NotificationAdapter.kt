package com.github.bumblebee202111.doubean.feature.notifications

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.res.dimensionResource
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemPostNotificationBinding
import com.github.bumblebee202111.doubean.model.RecommendedPostNotificationItem
import com.github.bumblebee202111.doubean.ui.common.UserProfileImage

class NotificationAdapter(private val onItemClick: (notification: RecommendedPostNotificationItem) -> Unit) :
    PagingDataAdapter<RecommendedPostNotificationItem, NotificationAdapter.ViewHolder>(
        NotificationDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPostNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { notificationItem ->
            holder.bind(item = notificationItem) { onItemClick(notificationItem) }
        }
    }

    class ViewHolder(private val binding: ListItemPostNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecommendedPostNotificationItem, cardOnClickListener: View.OnClickListener) {
            binding.apply {
                post = item
                
                groupAvatar.setContent {
                    UserProfileImage(
                        url = item.group.avatarUrl,
                        size = dimensionResource(id = R.dimen.icon_size_extra_small)
                    )
                }
                card.setOnClickListener(cardOnClickListener)
                executePendingBindings()
            }
        }

    }

}

private class NotificationDiffCallback : DiffUtil.ItemCallback<RecommendedPostNotificationItem>() {
    override fun areItemsTheSame(
        oldItem: RecommendedPostNotificationItem,
        newItem: RecommendedPostNotificationItem,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: RecommendedPostNotificationItem,
        newItem: RecommendedPostNotificationItem,
    ): Boolean {
        return oldItem == newItem
    }
}