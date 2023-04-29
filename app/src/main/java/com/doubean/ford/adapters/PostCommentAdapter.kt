package com.doubean.ford.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.R
import com.doubean.ford.data.vo.Post
import com.doubean.ford.data.vo.PostComment
import com.doubean.ford.databinding.ListItemPostCommentBinding
import com.doubean.ford.util.ShareUtil

class PostCommentAdapter(private val post: Post) :
    ListAdapter<PostComment, PostCommentAdapter.ViewHolder>(PostCommentDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPostCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), post
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }

    private class PostCommentDiffCallback : DiffUtil.ItemCallback<PostComment>() {
        override fun areItemsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
            return oldItem === newItem
        }
    }

    class ViewHolder(private val binding: ListItemPostCommentBinding, private val post: Post) :
        RecyclerView.ViewHolder(binding.root) {
        var shareText = ""

        init {
            post.group?.let { binding.authorOp.setTextColor(it.color) }
            post.group?.let { binding.repliedToAuthorOp.setTextColor(it.color) }
            binding.showPopup = View.OnClickListener { v: View ->
                val popupMenu = PopupMenu(v.context, binding.more)
                popupMenu.inflate(R.menu.menu_post_item)
                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    val postComment = binding.postComment
                    postComment?.let {
                        when (item.itemId) {

                            R.id.action_share -> {

                                shareText = post.group!!.name
                                val context = v.context
                                if (post.tagId != null) {
                                    shareText += "|" + post.tagName
                                }
                                shareText += """@${it.author.name}${context.getString(R.string.colon)} ${it.text}
"""
                                if (it.repliedTo != null) {
                                    shareText += """
                        ${context.getString(R.string.repliedTo)}@${it.repliedTo.author.name}： ${it.repliedTo.text}
                        
                        """.trimIndent()
                                }
                                shareText += """${context.getString(R.string.post)}@${post.author.name}${
                                    context.getString(
                                        R.string.colon
                                    )
                                } ${post.title} ${post.url}
"""
                                ShareUtil.Share(context, shareText)
                                return@setOnMenuItemClickListener true
                            }
                            else -> return@setOnMenuItemClickListener false
                        }

                    }
                    return@setOnMenuItemClickListener false
                }
                popupMenu.show()
            }
        }

        fun bind(item: PostComment) {
            binding.postComment = item
            binding.isAuthorOp = item.author.id == post.author.id
            if (item.photos.isNullOrEmpty()) {
                val adapter = PhotoAdapter()
                binding.photos.adapter = adapter
                adapter.submitList(item.photos)
            }
            if (item.repliedTo != null) {
                if (!item.repliedTo.photos.isNullOrEmpty()) {
                    val adapter = PhotoAdapter()
                    binding.repliedToPhotos.adapter = adapter
                    adapter.submitList(item.repliedTo.photos)
                    binding.isRepliedToAuthorOp = item.repliedTo.id == post.author.id
                }
            }
            binding.executePendingBindings()
        }
    }
}