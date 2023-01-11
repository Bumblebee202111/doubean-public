package com.doubean.ford.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doubean.ford.R;
import com.doubean.ford.data.vo.Post;
import com.doubean.ford.data.vo.PostComment;
import com.doubean.ford.databinding.ListItemPostCommentBinding;
import com.doubean.ford.util.ShareUtil;

public class PostCommentAdapter extends ListAdapter<PostComment, PostCommentAdapter.ViewHolder> {

    private final Post post;

    public PostCommentAdapter(Post post) {
        super(new PostCommentDiffCallback());
        this.post = post;
    }

    @NonNull
    @Override
    public PostCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemPostCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), post);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentAdapter.ViewHolder holder, int position) {
        PostComment comment = getItem(position);
        holder.bind(comment);
    }

    private static class PostCommentDiffCallback extends DiffUtil.ItemCallback<PostComment> {
        @Override
        public boolean areItemsTheSame(@NonNull PostComment oldItem, @NonNull PostComment newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull PostComment oldItem, @NonNull PostComment newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemPostCommentBinding binding;
        private final Post post;
        String shareText = "";

        public ViewHolder(@NonNull ListItemPostCommentBinding binding, Post post) {
            super(binding.getRoot());
            this.binding = binding;
            this.post = post;
            binding.authorOp.setTextColor(post.group.getColor());
            binding.repliedToAuthorOp.setTextColor(post.group.getColor());
            binding.setShowPopup(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), binding.more);
                popupMenu.inflate(R.menu.menu_post_item);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_share:
                            PostComment postComment = binding.getPostComment();
                            shareText = post.group.name;
                            Context context = v.getContext();
                            if (post.tagId != null) {
                                shareText += "|" + post.getTagName();
                            }
                            shareText += '@' + postComment.author.name + context.getString(R.string.colon) + ' ' +
                                    postComment.text + "\r\n";
                            if (postComment.repliedTo != null) {
                                shareText += context.getString(R.string.repliedTo) + '@' + postComment.repliedTo.author.name + "： " + postComment.repliedTo.text + "\r\n";
                            }
                            shareText += context.getString(R.string.post) + '@' + post.author.name + context.getString(R.string.colon) + ' ' +
                                    post.title + " " + post.url + "\r\n";
                            ShareUtil.Share(context, shareText);
                            return true;
                        default:
                            return false;
                    }

                });
                popupMenu.show();
            });
        }

        void bind(PostComment item) {
            binding.setPostComment(item);
            binding.setIsAuthorOp(item.author.id.equals(post.author.id));

            if (!item.photos.isEmpty()) {
                PhotoAdapter adapter = new PhotoAdapter();
                binding.photos.setAdapter(adapter);
                adapter.submitList(item.photos);
            }

            if (item.repliedTo != null) {
                if (!item.repliedTo.photos.isEmpty()) {
                    PhotoAdapter adapter = new PhotoAdapter();
                    binding.repliedToPhotos.setAdapter(adapter);
                    adapter.submitList(item.repliedTo.photos);
                    binding.setIsRepliedToAuthorOp(item.repliedTo.id.equals(post.author.id));
                }
            }
            binding.executePendingBindings();
        }
    }
}