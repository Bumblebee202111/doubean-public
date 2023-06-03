package com.doubean.ford.ui.groups.groupsHome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.databinding.ListItemGroupFollowBinding
import com.doubean.ford.model.GroupFollowItem

class GroupFollowAdapter : ListAdapter<GroupFollowItem, GroupFollowAdapter.ViewHolder>(
    GroupFollowDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemGroupFollowBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(private val binding: ListItemGroupFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = View.OnClickListener { v: View -> binding.groupFollow?.let{navigateToGroup(it, v)} }
        }

        private fun navigateToGroup(followItem: GroupFollowItem, itemView: View) {
            val direction =
                GroupsHomeFragmentDirections.actionGroupsToGroupDetail(followItem.groupId)
                    .setDefaultTabId(followItem.tabId)
            findNavController(itemView).navigate(direction)
        }

        fun bind(item: GroupFollowItem) {
            binding.groupFollow = item
            binding.executePendingBindings()
        }
    }
}

private class GroupFollowDiffCallback : DiffUtil.ItemCallback<GroupFollowItem>() {
    override fun areItemsTheSame(oldItem: GroupFollowItem, newItem: GroupFollowItem): Boolean {
        return oldItem.groupId == newItem.groupId && oldItem.tabId == newItem.tabId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: GroupFollowItem, newItem: GroupFollowItem): Boolean {
        return oldItem == newItem
    }
}