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

import com.doubean.ford.data.vo.GroupItem;
import com.doubean.ford.databinding.ListItemGroupBinding;
import com.doubean.ford.ui.groups.groupSearch.GroupSearchFragmentDirections;

public class GroupAdapter extends ListAdapter<GroupItem, GroupAdapter.ViewHolder> {

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
        GroupItem group = getItem(position);
        holder.bind(group);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<GroupItem> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupItem oldItem, @NonNull GroupItem newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupItem oldItem, @NonNull GroupItem newItem) {
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

        private void navigateToGroup(@NonNull GroupItem group, View itemView) {
            GroupSearchFragmentDirections.ActionGroupSearchToGroupDetail direction = GroupSearchFragmentDirections.actionGroupSearchToGroupDetail(group.id);
            Navigation.findNavController(itemView).navigate(direction);

        }

        void bind(GroupItem item) {
            binding.setGroup(item);
            binding.executePendingBindings();
        }
    }
}
