package com.github.bumblebee202111.doubean.feature.groups.groupSearch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.Navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemGroupBinding
import com.github.bumblebee202111.doubean.model.GroupSearchResultGroupItem

class SearchResultGroupAdapter :
    PagingDataAdapter<GroupSearchResultGroupItem, SearchResultGroupAdapter.ViewHolder>(
        SearchResultGroupDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemGroupBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group)
    }

    class ViewHolder(private val binding: ListItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clickListener = View.OnClickListener {
                    v: View -> binding.group?.let{navigateToGroup(it, v)} }
        }

        private fun navigateToGroup(group: GroupSearchResultGroupItem, itemView: View) {
            val direction = GroupSearchFragmentDirections.actionGroupSearchToGroupDetail(group.id)
            findNavController(itemView).navigate(direction)
        }

        fun bind(item: GroupSearchResultGroupItem?) {
            binding.group = item
            binding.avatar.setContent {
                AsyncImage(
                    model = item?.avatarUrl,
                    contentDescription = stringResource(id = R.string.a11y_group_item_image),
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.icon_size_extra_large))
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_small))),
                    contentScale = ContentScale.Crop
                )
            }
            binding.executePendingBindings()
        }
    }
}

class SearchResultGroupDiffCallback : DiffUtil.ItemCallback<GroupSearchResultGroupItem>() {
    override fun areItemsTheSame(
        oldItem: GroupSearchResultGroupItem,
        newItem: GroupSearchResultGroupItem,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: GroupSearchResultGroupItem,
        newItem: GroupSearchResultGroupItem,
    ): Boolean {
        return oldItem == newItem
    }
}
