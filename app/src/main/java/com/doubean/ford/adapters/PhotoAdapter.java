package com.doubean.ford.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doubean.ford.data.vo.SizedPhoto;
import com.doubean.ford.databinding.ListItemPhotoBinding;

public class PhotoAdapter extends ListAdapter<SizedPhoto, PhotoAdapter.ViewHolder> {


    public PhotoAdapter() {
        super(new PhotoDiffCallback());
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemPhotoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {
        SizedPhoto photo = getItem(position);
        holder.bind(photo);
    }

    private static class PhotoDiffCallback extends DiffUtil.ItemCallback<SizedPhoto> {
        @Override
        public boolean areItemsTheSame(@NonNull SizedPhoto oldItem, @NonNull SizedPhoto newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull SizedPhoto oldItem, @NonNull SizedPhoto newItem) {
            return oldItem == newItem;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemPhotoBinding binding;

        public ViewHolder(@NonNull ListItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SizedPhoto item) {
            binding.setPhoto(item);
            //binding.image.setLayoutParams(new ViewGroup.LayoutParams(item.image.normal.width,item.image.normal.height));
            binding.executePendingBindings();
        }
    }
}