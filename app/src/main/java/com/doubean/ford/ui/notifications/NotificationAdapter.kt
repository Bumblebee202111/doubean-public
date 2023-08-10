package com.doubean.ford.ui.notifications

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.databinding.ListItemPostNotificationBinding
import com.doubean.ford.model.RecommendedPostNotificationItem

class NotificationAdapter :
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
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(private val binding: ListItemPostNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecommendedPostNotificationItem) {
            binding.post = item
            binding.executePendingBindings()
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