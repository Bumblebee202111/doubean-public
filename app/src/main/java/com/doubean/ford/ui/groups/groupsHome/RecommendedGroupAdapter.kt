package com.doubean.ford.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.databinding.ListItemRecommendedGroupBinding
import com.doubean.ford.model.RecommendedGroupItem
import com.doubean.ford.model.RecommendedGroupItemGroup
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeFragmentDirections
import com.doubean.ford.util.TopItemNoBackgroundUtil

class RecommendedGroupAdapter(private val context: Context) :
    ListAdapter<RecommendedGroupItem, RecommendedGroupAdapter.ViewHolder>(
        RecommendedGroupDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemRecommendedGroupBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = getItem(position)
        @ColorInt val color = TopItemNoBackgroundUtil.getNoBackground(context, position, itemCount)
        holder.bind(group, color)
    }


    class ViewHolder(private val binding: ListItemRecommendedGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = View.OnClickListener { v: View -> binding.recommendedGroup?.let{navigateToGroup(it.group, v)} }
        }

        private fun navigateToGroup(group: RecommendedGroupItemGroup, itemView: View) {
            val direction = GroupsHomeFragmentDirections.actionGroupsToGroupDetail(group.id)
            findNavController(itemView).navigate(direction)
        }

        fun bind(item: RecommendedGroupItem, @ColorInt color: Int) {
            binding.recommendedGroup = item
            binding.noBackground = color
            binding.executePendingBindings()
        }
    }
}

private class RecommendedGroupDiffCallback : DiffUtil.ItemCallback<RecommendedGroupItem>() {
    override fun areItemsTheSame(
        oldItem: RecommendedGroupItem,
        newItem: RecommendedGroupItem,
    ): Boolean {
        return oldItem.group.id == newItem.group.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: RecommendedGroupItem,
        newItem: RecommendedGroupItem,
    ): Boolean {
        return oldItem == newItem
    }
}