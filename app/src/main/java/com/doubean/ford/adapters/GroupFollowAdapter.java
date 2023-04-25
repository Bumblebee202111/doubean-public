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

import com.doubean.ford.data.vo.GroupFollowItem;
import com.doubean.ford.databinding.ListItemGroupFollowBinding;
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeFragmentDirections;

import java.util.Objects;

public class GroupFollowAdapter extends ListAdapter<GroupFollowItem, GroupFollowAdapter.ViewHolder> {

    public GroupFollowAdapter() {
        super(new GroupDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemGroupFollowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupFollowItem item = getItem(position);
        holder.bind(item);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<GroupFollowItem> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupFollowItem oldItem, @NonNull GroupFollowItem newItem) {
            return oldItem.getGroupId().equals(newItem.getGroupId()) && Objects.equals(oldItem.getGroupTabId(), newItem.getGroupTabId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupFollowItem oldItem, @NonNull GroupFollowItem newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemGroupFollowBinding binding;

        public ViewHolder(@NonNull ListItemGroupFollowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> navigateToGroup(binding.getGroupFollow(), v));

        }

        private void navigateToGroup(@NonNull GroupFollowItem followItem, View itemView) {
            GroupsHomeFragmentDirections.ActionGroupsToGroupDetail direction = GroupsHomeFragmentDirections.actionGroupsToGroupDetail(followItem.getGroupId()).setDefaultTabId(followItem.getGroupTabId());
            Navigation.findNavController(itemView).navigate(direction);
        }

        void bind(GroupFollowItem item) {
            binding.setGroupFollow(item);
            binding.executePendingBindings();
        }
    }
}
