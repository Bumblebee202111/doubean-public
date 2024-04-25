package com.github.bumblebee202111.doubean.feature.groups.postDetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemPostCommentBinding
import com.github.bumblebee202111.doubean.model.PostComment
import com.github.bumblebee202111.doubean.model.PostDetail
import com.github.bumblebee202111.doubean.model.Resource
import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.ui.component.ListItemImages
import com.github.bumblebee202111.doubean.util.ShareUtil

class PostCommentAdapter(
    private val postLiveData: LiveData<Resource<PostDetail>>,
    private val lifecycleOwner: LifecycleOwner,
    private val onImageClick: (image: SizedImage) -> Unit,
) :
    ListAdapter<PostComment, PostCommentAdapter.ViewHolder>(PostCommentDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemPostCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
        binding.lifecycleOwner = lifecycleOwner
        return ViewHolder(binding, postLiveData)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment, onImageClick)
    }

    class ViewHolder(
        private val binding: ListItemPostCommentBinding,
        private val postLiveData: LiveData<Resource<PostDetail>>,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            postLiveData.observe(binding.lifecycleOwner!!) {
                it.data?.let { post ->
                    post.group?.color?.let { color ->
                        binding.authorOp.setTextColor(color)
                        binding.repliedToAuthorOp.setTextColor(color)
                    }
                    binding.showPopup = View.OnClickListener { v ->
                        val popupMenu = PopupMenu(v.context, binding.more)
                        popupMenu.inflate(R.menu.menu_post_item)
                        popupMenu.setOnMenuItemClickListener { item ->
                            val postComment = binding.postComment
                            postComment?.let { comment ->
                                when (item.itemId) {
                                    R.id.action_share -> {
                                        val shareText = StringBuilder()
                                        post.group?.let { group -> shareText.append(group.name) }

                                        post.tag?.let { tag ->
                                            shareText.append("|" + tag.name)
                                        }
                                        val context = v.context
                                        shareText.append(
                                            "@${comment.author.name}${
                                                context.getString(R.string.colon)
                                            }${comment.text}"
                                        )

                                        comment.repliedTo?.let { repliedTo ->
                                            shareText.append("${context.getString(R.string.repliedTo)}@${repliedTo.author.name}： ${repliedTo.text}")
                                        }
                                        shareText.append(
                                            """${context.getString(R.string.post)}@${comment.author.name}${
                                                context.getString(R.string.colon)
                                            } 
                                            ${post.title} ${post.url}
                                            """
                                        )
                                        ShareUtil.share(context, shareText)
                                        true
                                    }

                                    else -> false
                                }

                            }
                            false
                        }
                        popupMenu.show()
                    }
                }
            }

        }

        fun bind(item: PostComment, onImageClick: (image: SizedImage) -> Unit) {
            binding.postComment = item
            postLiveData.observe(binding.lifecycleOwner!!) {
                it.data?.let { post ->
                    binding.isAuthorOp = item.author.id == post.author.id
                    if (!item.photos.isNullOrEmpty()) {
                        binding.photos.apply {
                            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                            setContent {
                                ListItemImages(
                                    images = item.photos.map(SizedPhoto::image),
                                    onImageClick = onImageClick
                                )
                            }
                        }
                    }
                    if (item.repliedTo != null) {
                        if (!item.repliedTo.photos.isNullOrEmpty()) {
                            binding.repliedToPhotos.apply {
                                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                                setContent {
                                    ListItemImages(
                                        images = item.repliedTo.photos.map(SizedPhoto::image),
                                        onImageClick = onImageClick
                                    )
                                }
                            }
                            binding.isRepliedToAuthorOp = item.repliedTo.id == post.author.id
                        }
                    }
                }

            }
            binding.executePendingBindings()
        }
    }
}

private class PostCommentDiffCallback : DiffUtil.ItemCallback<PostComment>() {
    override fun areItemsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
        return oldItem == newItem
    }
}