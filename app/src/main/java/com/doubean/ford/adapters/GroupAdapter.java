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

import com.doubean.ford.data.Group;
import com.doubean.ford.databinding.ListItemGroupBinding;
import com.doubean.ford.ui.groups.GroupsFragmentDirections;

public class GroupAdapter extends ListAdapter<Group, GroupAdapter.ViewHolder> {

    public GroupAdapter() {
        super(new GroupDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = getItem(position);
        holder.bind(group);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<Group> {
        @Override
        public boolean areItemsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemGroupBinding binding;

        public ViewHolder(@NonNull ListItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> navigateToGroup(binding.getGroup(), v));
        }

        private void navigateToGroup(@NonNull Group group, View itemView) {
            GroupsFragmentDirections.ActionNavigationGroupsToNavigationGroupDetail direction =
                    GroupsFragmentDirections.actionNavigationGroupsToNavigationGroupDetail(group.getId());
            Navigation.findNavController(itemView).navigate(direction);
        }

        void bind(Group item) {
            binding.setGroup(item);
            binding.executePendingBindings();
        }
    }
}
