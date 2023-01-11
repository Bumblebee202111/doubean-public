package com.doubean.ford.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doubean.ford.R;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.PostItem;
import com.doubean.ford.databinding.ListItemPostBinding;
import com.doubean.ford.ui.groups.groupDetail.GroupDetailFragmentDirections;
import com.doubean.ford.util.ShareUtil;

public class PostAdapter extends ListAdapter<PostItem, PostAdapter.ViewHolder> {
    private final GroupDetail group;

    public PostAdapter(GroupDetail group) {
        super(new PostAdapter.GroupDiffCallback());
        this.group = group;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), group);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        PostItem post = getItem(position);
        holder.bind(post);
    }

    private static class GroupDiffCallback extends DiffUtil.ItemCallback<PostItem> {
        @Override
        public boolean areItemsTheSame(@NonNull PostItem oldItem, @NonNull PostItem newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull PostItem oldItem, @NonNull PostItem newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemPostBinding binding;
        String shareText = "";

        public ViewHolder(@NonNull ListItemPostBinding binding, GroupDetail group) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setClickListener(v -> navigateToPost(binding.getPost(), v));
            binding.setShowPopup(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), binding.more);
                popupMenu.inflate(R.menu.menu_post_item);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_share:
                            PostItem post = binding.getPost();
                            Context context = v.getContext();
                            shareText = group.name;
                            if (post.tagId != null) {
                                shareText += "|" + post.getTagName();
                            }
                            shareText += '@' + post.author.name + context.getString(R.string.colon) + ' ' +
                                    post.title + ' ' + post.url + "\r\n";
                            ShareUtil.Share(context, shareText);

                            return true;
                        default:
                            return false;
                    }

                });
                popupMenu.show();
            });

        }

        private void navigateToPost(@NonNull PostItem post, View itemView) {
            GroupDetailFragmentDirections.ActionNavigationGroupDetailToNavigationPostDetail direction =
                    GroupDetailFragmentDirections.actionNavigationGroupDetailToNavigationPostDetail(post.id);
            Navigation.findNavController(itemView).navigate(direction);
        }


        void bind(PostItem item) {
            binding.setPost(item);
            binding.executePendingBindings();
        }
    }
}