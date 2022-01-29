package com.doubean.ford.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doubean.ford.data.Group;
import com.doubean.ford.databinding.ListItemGroupBinding;

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
            return oldItem.getGroupId().equals(newItem.getGroupId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem == newItem;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ListItemGroupBinding binding;

        public ViewHolder(@NonNull ListItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(group ->
                    navigateToGroup(group, itemView));
        }

        private void navigateToGroup(View group, View itemView) {

        }

        void bind(Group item) {
            binding.setGroup(item);
            binding.executePendingBindings();
        }
    }
}
