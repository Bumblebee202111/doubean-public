package com.doubean.ford.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.data.vo.GroupItem
import com.doubean.ford.databinding.ListItemGroupBinding
import com.doubean.ford.ui.groups.groupSearch.GroupSearchFragmentDirections

class GroupAdapter : ListAdapter<GroupItem, GroupAdapter.ViewHolder>(GroupDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group)
    }

    private class GroupDiffCallback : DiffUtil.ItemCallback<GroupItem>() {
        override fun areItemsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem === newItem
        }
    }

    class ViewHolder(private val binding: ListItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = View.OnClickListener {
                    v: View -> binding.group?.let{navigateToGroup(it, v)} }
        }

        private fun navigateToGroup(group: GroupItem, itemView: View) {
            val direction = GroupSearchFragmentDirections.actionGroupSearchToGroupDetail(group.id)
            findNavController(itemView).navigate(direction)
        }

        fun bind(item: GroupItem) {
            binding.group = item
            binding.executePendingBindings()
        }
    }
}