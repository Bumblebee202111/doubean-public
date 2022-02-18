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

import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.GroupTopicComment;
import com.doubean.ford.databinding.ListItemGroupTopicCommentBinding;
import com.doubean.ford.ui.groupDetail.GroupDetailFragmentDirections;

public class GroupTopicCommentAdapter extends ListAdapter<GroupTopicComment, GroupTopicCommentAdapter.ViewHolder> {

    public GroupTopicCommentAdapter() {
        super(new GroupTopicCommentDiffCallback());
    }

    @NonNull
    @Override
    public GroupTopicCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemGroupTopicCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupTopicCommentAdapter.ViewHolder holder, int position) {
        GroupTopicComment topic = getItem(position);
        holder.bind(topic);
    }

    private static class GroupTopicCommentDiffCallback extends DiffUtil.ItemCallback<GroupTopicComment> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupTopicComment oldItem, @NonNull GroupTopicComment newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupTopicComment oldItem, @NonNull GroupTopicComment newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemGroupTopicCommentBinding binding;

        public ViewHolder(@NonNull ListItemGroupTopicCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void navigateToTopic(@NonNull GroupTopic topic, View itemView) {
            GroupDetailFragmentDirections.ActionNavigationGroupDetailToNavigationGroupTopicDetail direction =
                    GroupDetailFragmentDirections.actionNavigationGroupDetailToNavigationGroupTopicDetail(topic.topicId);
            Navigation.findNavController(itemView).navigate(direction);
        }

        void bind(GroupTopicComment item) {
            binding.setGroupTopicComment(item);
            binding.executePendingBindings();
        }
    }
}