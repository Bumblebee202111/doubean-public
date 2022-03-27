package com.doubean.ford.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doubean.ford.data.vo.GroupPost;
import com.doubean.ford.databinding.ListItemGroupPostBinding;
import com.doubean.ford.ui.groups.groupDetail.GroupDetailFragmentDirections;

public class GroupPostAdapter extends ListAdapter<GroupPost, GroupPostAdapter.ViewHolder> {

    public GroupPostAdapter() {
        super(new GroupPostAdapter.GroupDiffCallback());
    }

    @NonNull
    @Override
    public GroupPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemGroupPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupPostAdapter.ViewHolder holder, int position) {
        GroupPost post = getItem(position);
        holder.bind(post);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<GroupPost> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupPost oldItem, @NonNull GroupPost newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupPost oldItem, @NonNull GroupPost newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemGroupPostBinding binding;

        public ViewHolder(@NonNull ListItemGroupPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> navigateToPost(binding.getGroupPost(), v));
        }

        private void navigateToPost(@NonNull GroupPost post, View itemView) {
            GroupDetailFragmentDirections.ActionNavigationGroupDetailToNavigationPostDetail direction =
                    GroupDetailFragmentDirections.actionNavigationGroupDetailToNavigationPostDetail(post.id);
            Navigation.findNavController(itemView).navigate(direction);
        }

        void bind(GroupPost item) {
            binding.setGroupPost(item);
            binding.executePendingBindings();
        }
    }
}