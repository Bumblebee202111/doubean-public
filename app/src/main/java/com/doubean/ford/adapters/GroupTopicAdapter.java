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
import com.doubean.ford.databinding.ListItemGroupTopicBinding;
import com.doubean.ford.ui.groups.GroupsFragmentDirections;

public class GroupTopicAdapter extends ListAdapter<GroupTopic, GroupTopicAdapter.ViewHolder> {

    public GroupTopicAdapter() {
        super(new GroupTopicAdapter.GroupDiffCallback());
    }

    @NonNull
    @Override
    public GroupTopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemGroupTopicBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupTopicAdapter.ViewHolder holder, int position) {
        GroupTopic topic = getItem(position);
        holder.bind(topic);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<GroupTopic> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupTopic oldItem, @NonNull GroupTopic newItem) {
            return oldItem.topicId.equals(newItem.topicId);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupTopic oldItem, @NonNull GroupTopic newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemGroupTopicBinding binding;

        public ViewHolder(@NonNull ListItemGroupTopicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> navigateToTopic(binding.getGroupTopic(), v));
        }

        private void navigateToTopic(@NonNull GroupTopic topic, View itemView) {
            GroupsFragmentDirections.ActionNavigationGroupsToNavigationGroupDetail direction =
                    GroupsFragmentDirections.actionNavigationGroupsToNavigationGroupDetail(topic.topicId);
            Navigation.findNavController(itemView).navigate(direction);
        }

        void bind(GroupTopic item) {
            binding.setGroupTopic(item);
            binding.executePendingBindings();
        }
    }
}