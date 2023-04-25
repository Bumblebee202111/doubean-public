package com.doubean.ford.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doubean.ford.data.vo.GroupItem;
import com.doubean.ford.data.vo.RecommendedGroup;
import com.doubean.ford.databinding.ListItemRecommendedGroupBinding;
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeFragmentDirections;
import com.doubean.ford.util.TopItemNoBackgroundUtil;

public class RecommendedGroupAdapter extends ListAdapter<RecommendedGroup, RecommendedGroupAdapter.ViewHolder> {

    private Context context;

    public RecommendedGroupAdapter(Context context) {
        super(new RecommendedGroupDiffCallback());
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ListItemRecommendedGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendedGroup group = getItem(position);
        @ColorInt int color = TopItemNoBackgroundUtil.getNoBackground(context, position, getItemCount());
        holder.bind(group, color);
    }

    private static class RecommendedGroupDiffCallback extends DiffUtil.ItemCallback<RecommendedGroup> {
        @Override
        public boolean areItemsTheSame(@NonNull RecommendedGroup oldItem, @NonNull RecommendedGroup newItem) {
            return oldItem.group.id.equals(newItem.group.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull RecommendedGroup oldItem, @NonNull RecommendedGroup newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemRecommendedGroupBinding binding;

        public ViewHolder(@NonNull ListItemRecommendedGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.setClickListener(v -> navigateToGroup(binding.getRecommendedGroup().group, v));
        }

        private void navigateToGroup(@NonNull GroupItem group, View itemView) {
            GroupsHomeFragmentDirections.ActionGroupsToGroupDetail direction = GroupsHomeFragmentDirections.actionGroupsToGroupDetail(group.id);
            Navigation.findNavController(itemView).navigate(direction);

        }

        void bind(RecommendedGroup item, @ColorInt int color) {
            binding.setRecommendedGroup(item);
            binding.setNoBackground(color);
            binding.executePendingBindings();
        }
    }
}