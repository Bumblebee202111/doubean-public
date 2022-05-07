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

import com.doubean.ford.data.vo.GroupFollowedItem;
import com.doubean.ford.databinding.ListItemGroupFollowedBinding;
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeFragmentDirections;

import java.util.Objects;

public class GroupFollowedAdapter extends ListAdapter<GroupFollowedItem, GroupFollowedAdapter.ViewHolder> {

    public GroupFollowedAdapter() {
        super(new GroupDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemGroupFollowedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupFollowedItem followed = getItem(position);
        holder.bind(followed);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<GroupFollowedItem> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupFollowedItem oldItem, @NonNull GroupFollowedItem newItem) {
            return oldItem.getGroupId().equals(newItem.getGroupId()) && Objects.equals(oldItem.getGroupTabId(), newItem.getGroupTabId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupFollowedItem oldItem, @NonNull GroupFollowedItem newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemGroupFollowedBinding binding;

        public ViewHolder(@NonNull ListItemGroupFollowedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> navigateToGroup(binding.getGroupFollowed(), v));

        }

        private void navigateToGroup(@NonNull GroupFollowedItem followedItem, View itemView) {
            GroupsHomeFragmentDirections.ActionNavigationGroupsToNavigationGroupDetail direction = GroupsHomeFragmentDirections.actionNavigationGroupsToNavigationGroupDetail(followedItem.getGroupId()).setDefaultTabId(followedItem.getGroupTabId());
            Navigation.findNavController(itemView).navigate(direction);
        }

        void bind(GroupFollowedItem item) {
            binding.setGroupFollowed(item);
            binding.executePendingBindings();
        }
    }
}
