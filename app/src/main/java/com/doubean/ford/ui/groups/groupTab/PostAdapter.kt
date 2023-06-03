package com.doubean.ford.ui.groups.groupTab

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.R
import com.doubean.ford.databinding.ListItemPostBinding
import com.doubean.ford.model.GroupDetail
import com.doubean.ford.model.PostItem
import com.doubean.ford.model.Resource
import com.doubean.ford.ui.groups.groupDetail.GroupDetailFragmentDirections
import com.doubean.ford.util.ShareUtil

class PostAdapter(private val groupLiveData: LiveData<Resource<GroupDetail>>) :
    ListAdapter<PostItem, PostAdapter.ViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), groupLiveData
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class ViewHolder(
        private val binding: ListItemPostBinding,
        groupLiveData: LiveData<Resource<GroupDetail>>,
    ) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.clickListener =
                View.OnClickListener { v -> binding.post?.let { navigateToPost(it, v) } }
            binding.showPopup = View.OnClickListener { v ->
                val popupMenu = PopupMenu(v.context, binding.more)
                popupMenu.inflate(R.menu.menu_post_item)
                popupMenu.setOnMenuItemClickListener { item ->
                    val post = binding.post
                    post?.let {
                        when (item.itemId) {
                            R.id.action_share -> {
                                val context = v.context
                                val shareText = StringBuilder()
                                groupLiveData.value?.data?.let { group ->
                                    shareText.append(group.name + "|")
                                }

                                it.tag?.let { tag ->
                                    shareText.append(tag.name)
                                }
                                shareText.append("@${it.author.name}${context.getString(R.string.colon)} ${it.title} ${it.url}")
                                ShareUtil.share(context, shareText)
                                false
                            }
                            else -> false
                        }
                    }
                    false
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

class PostDiffCallback : DiffUtil.ItemCallback<PostItem>() {
    override fun areItemsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
        return oldItem == newItem
    }
}