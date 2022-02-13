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

import com.doubean.ford.MobileNavigationDirections;
import com.doubean.ford.data.GroupFavoriteDetail;
import com.doubean.ford.databinding.ListItemGroupFavoriteBinding;

import java.util.Objects;

public class GroupFavoriteAdapter extends ListAdapter<GroupFavoriteDetail, GroupFavoriteAdapter.ViewHolder> {

    public GroupFavoriteAdapter() {
        super(new GroupDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemGroupFavoriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupFavoriteDetail favorite = getItem(position);
        holder.bind(favorite);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<GroupFavoriteDetail> {
        @Override
        public boolean areItemsTheSame(@NonNull GroupFavoriteDetail oldItem, @NonNull GroupFavoriteDetail newItem) {
            return oldItem.getGroupId().equals(newItem.getGroupId()) && Objects.equals(oldItem.getGroupTabId(), newItem.getGroupTabId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull GroupFavoriteDetail oldItem, @NonNull GroupFavoriteDetail newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemGroupFavoriteBinding binding;

        public ViewHolder(@NonNull ListItemGroupFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> navigateToGroup(binding.getFavorite(), v));
        }

        private void navigateToGroup(@NonNull GroupFavoriteDetail favorite, View itemView) {
            MobileNavigationDirections.ActionGlobalNavigationGroupDetail direction =
                    MobileNavigationDirections.actionGlobalNavigationGroupDetail(favorite.getGroupId(), favorite.getGroupTabId());
            Navigation.findNavController(itemView).navigate(direction);
        }

        void bind(GroupFavoriteDetail item) {
            binding.setFavorite(item);
            binding.executePendingBindings();
        }
    }
}
