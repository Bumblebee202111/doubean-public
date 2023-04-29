package com.doubean.ford.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.R
import com.doubean.ford.data.vo.GroupDetail
import com.doubean.ford.data.vo.PostItem
import com.doubean.ford.databinding.ListItemPostBinding
import com.doubean.ford.ui.groups.groupDetail.GroupDetailFragmentDirections
import com.doubean.ford.util.ShareUtil

class PostAdapter(private val group: GroupDetail) :
    ListAdapter<PostItem, PostAdapter.ViewHolder>(GroupDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), group
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    private class GroupDiffCallback : DiffUtil.ItemCallback<PostItem>() {
        override fun areItemsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
            return oldItem === newItem
        }
    }

    class ViewHolder(private val binding: ListItemPostBinding, group: GroupDetail) :
        RecyclerView.ViewHolder(binding.root) {
        var shareText = ""

        init {
            binding.clickListener =
                View.OnClickListener { v: View -> binding.post?.let { navigateToPost(it, v) } }
            binding.showPopup = View.OnClickListener { v: View ->
                val popupMenu = PopupMenu(v.context, binding.more)
                popupMenu.inflate(R.menu.menu_post_item)
                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    val post = binding.post
                    post?.let {
                        when (item.itemId) {
                            R.id.action_share -> {

                                val context = v.context
                                shareText = group.name
                                if (it.tagId != null) {
                                    shareText += "|" + it.tagName
                                }
                                shareText += """@${it.author.name}${context.getString(R.string.colon)} ${it.title} ${it.url}
"""
                                ShareUtil.Share(context, shareText)
                                return@setOnMenuItemClickListener false
                            }
                            else -> return@setOnMenuItemClickListener false
                        }
                    }
                    return@setOnMenuItemClickListener false
                }
                popupMenu.show()
            }
        }

        private fun navigateToPost(post: PostItem, itemView: View) {
            val direction = GroupDetailFragmentDirections.actionGroupDetailToPostDetail(post.id)
            findNavController(itemView).navigate(direction)
        }

        fun bind(item: PostItem) {
            binding.post = item
            binding.executePendingBindings()
        }
    }
}