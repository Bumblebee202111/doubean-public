package com.doubean.ford.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doubean.ford.data.vo.GroupPostComment;
import com.doubean.ford.databinding.ListItemGroupPostCommentBinding;

public class PostCommentAdapter extends ListAdapter<GroupPostComment, PostCommentAdapter.ViewHolder> {

    private String op;

    public PostCommentAdapter(String op) {
        super(new PostCommentDiffCallback());
        this.op = op;
    }

    @NonNull
    @Override
    public PostCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemGroupPostCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), op);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentAdapter.ViewHolder holder, int position) {
        GroupPostComment comment = getItem(position);
        holder.bind(comment);
    }

    private static class PostCommentDiffCallback extends DiffUtil.ItemCallback<GroupPostComment> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupPostComment oldItem, @NonNull GroupPostComment newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupPostComment oldItem, @NonNull GroupPostComment newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemGroupPostCommentBinding binding;
        private final String opId;

        public ViewHolder(@NonNull ListItemGroupPostCommentBinding binding, String opId) {
            super(binding.getRoot());
            this.binding = binding;
            this.opId = opId;
        }

        void bind(GroupPostComment item) {
            binding.setPostComment(item);
            binding.executePendingBindings();
            if (!item.photos.isEmpty()) {
                PhotoAdapter adapter = new PhotoAdapter();
                binding.photos.setAdapter(adapter);
                adapter.submitList(item.photos);
            }
            if (item.repliedTo != null && !item.repliedTo.photos.isEmpty()) {
                PhotoAdapter adapter = new PhotoAdapter();
                binding.repliedToPhotos.setAdapter(adapter);
                adapter.submitList(item.repliedTo.photos);
            }
            binding.setAuthorOp(item.author.id.equals(opId));
        }
    }
}