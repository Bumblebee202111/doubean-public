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

public class GroupPostCommentAdapter extends ListAdapter<GroupPostComment, GroupPostCommentAdapter.ViewHolder> {

    public GroupPostCommentAdapter() {
        super(new GroupPostCommentDiffCallback());
    }

    @NonNull
    @Override
    public GroupPostCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemGroupPostCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupPostCommentAdapter.ViewHolder holder, int position) {
        GroupPostComment comment = getItem(position);
        holder.bind(comment);
    }

    private static class GroupPostCommentDiffCallback extends DiffUtil.ItemCallback<GroupPostComment> {
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

        private ListItemGroupPostCommentBinding binding;

        public ViewHolder(@NonNull ListItemGroupPostCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        void bind(GroupPostComment item) {
            binding.setGroupPostComment(item);
            binding.executePendingBindings();
        }
    }
}