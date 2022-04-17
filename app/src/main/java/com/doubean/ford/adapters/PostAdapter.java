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

import com.doubean.ford.data.vo.GroupPostItem;
import com.doubean.ford.databinding.ListItemGroupPostBinding;
import com.doubean.ford.ui.groups.groupDetail.GroupDetailFragmentDirections;

public class PostAdapter extends ListAdapter<GroupPostItem, PostAdapter.ViewHolder> {

    public PostAdapter() {
        super(new PostAdapter.GroupDiffCallback());
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemGroupPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        GroupPostItem post = getItem(position);
        holder.bind(post);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<GroupPostItem> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupPostItem oldItem, @NonNull GroupPostItem newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupPostItem oldItem, @NonNull GroupPostItem newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemGroupPostBinding binding;

        public ViewHolder(@NonNull ListItemGroupPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> navigateToPost(binding.getPost(), v));
        }

        private void navigateToPost(@NonNull GroupPostItem post, View itemView) {
            GroupDetailFragmentDirections.ActionNavigationGroupDetailToNavigationPostDetail direction =
                    GroupDetailFragmentDirections.actionNavigationGroupDetailToNavigationPostDetail(post.id);
            Navigation.findNavController(itemView).navigate(direction);
        }

        void bind(GroupPostItem item) {
            binding.setPost(item);
            binding.executePendingBindings();
        }
    }
}