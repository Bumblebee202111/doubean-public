package com.doubean.ford.ui.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doubean.ford.databinding.ListItemPhotoBinding
import com.doubean.ford.model.SizedPhoto

class PhotoAdapter : ListAdapter<SizedPhoto, PhotoAdapter.ViewHolder>(PhotoDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = getItem(position)
        holder.bind(photo)
    }

    class ViewHolder(private val binding: ListItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SizedPhoto) {
            binding.photo = item
            //binding.image.setLayoutParams(new ViewGroup.LayoutParams(item.image.normal.width,item.image.normal.height));
            binding.executePendingBindings()
        }
    }
}

private class PhotoDiffCallback : DiffUtil.ItemCallback<SizedPhoto>() {
    override fun areItemsTheSame(oldItem: SizedPhoto, newItem: SizedPhoto): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SizedPhoto, newItem: SizedPhoto): Boolean {
        return oldItem == newItem
    }
}