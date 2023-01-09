package com.doubean.ford.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doubean.ford.data.vo.PostComment;
import com.doubean.ford.databinding.ListItemGroupPostCommentBinding;

public class PostCommentAdapter extends ListAdapter<PostComment, PostCommentAdapter.ViewHolder> {

    private String op;
    private Integer groupColor;

    public PostCommentAdapter(String op, Integer groupColor) {
        super(new PostCommentDiffCallback());
        this.op = op;
        this.groupColor = groupColor;
    }

    @NonNull
    @Override
    public PostCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemGroupPostCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), op, groupColor);
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

        private final ListItemGroupPostCommentBinding binding;
        private final String opId;

        public ViewHolder(@NonNull ListItemGroupPostCommentBinding binding, String opId, Integer groupColor) {
            super(binding.getRoot());
            this.binding = binding;
            this.opId = opId;
            binding.authorOp.setTextColor(groupColor);
            binding.repliedToAuthorOp.setTextColor(groupColor);
        }

        void bind(PostComment item) {
            if (item.created.toLocalDate() == null) {
                Log.d("doubean123", item.toString());
            }
            binding.setPostComment(item);
            binding.setIsAuthorOp(item.author.id.equals(opId));

            if (!item.photos.isEmpty()) {
                PhotoAdapter adapter = new PhotoAdapter();
                binding.photos.setAdapter(adapter);
                adapter.submitList(item.photos);
            }

            if (item.repliedTo != null) {
                if (item.repliedTo.created.toLocalDate() == null) {
                    Log.d("doubean", item.toString());
                }
                if (!item.repliedTo.photos.isEmpty()) {
                    PhotoAdapter adapter = new PhotoAdapter();
                    binding.repliedToPhotos.setAdapter(adapter);
                    adapter.submitList(item.repliedTo.photos);
                    binding.setIsRepliedToAuthorOp(item.repliedTo.id.equals(opId));
                }
            }
            binding.executePendingBindings();
        }
    }
}