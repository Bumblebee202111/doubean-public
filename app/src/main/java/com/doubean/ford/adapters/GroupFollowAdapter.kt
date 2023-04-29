package com.doubean.ford.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.data.vo.GroupFollowItem
import com.doubean.ford.databinding.ListItemGroupFollowBinding
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeFragmentDirections

class GroupFollowAdapter : ListAdapter<GroupFollowItem, GroupFollowAdapter.ViewHolder>(GroupDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemGroupFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    private class GroupDiffCallback : DiffUtil.ItemCallback<GroupFollowItem>() {
        override fun areItemsTheSame(oldItem: GroupFollowItem, newItem: GroupFollowItem): Boolean {
            return oldItem.groupId == newItem.groupId && oldItem.groupTabId == newItem.groupTabId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: GroupFollowItem, newItem: GroupFollowItem): Boolean {
            return oldItem === newItem
        }
    }

    class ViewHolder(private val binding: ListItemGroupFollowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = View.OnClickListener { v: View -> binding.groupFollow?.let{navigateToGroup(it, v)} }
        }

        private fun navigateToGroup(followItem: GroupFollowItem, itemView: View) {
            val direction = GroupsHomeFragmentDirections.actionGroupsToGroupDetail(followItem.groupId).setDefaultTabId(followItem.groupTabId)
            findNavController(itemView).navigate(direction)
        }

        fun bind(item: GroupFollowItem) {
            binding.groupFollow = item
            binding.executePendingBindings()
        }
    }
}