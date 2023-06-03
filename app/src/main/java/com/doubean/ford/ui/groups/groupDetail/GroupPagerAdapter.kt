package com.doubean.ford.ui.groups.groupDetail

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.doubean.ford.ui.groups.groupTab.GroupTabFragment

class GroupPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val groupId: String,
    taggedTabIds: List<String>?,
) : FragmentStateAdapter(
    fragmentManager, lifecycle
) {
    var taggedTabIds = taggedTabIds ?: emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun createFragment(position: Int) =
        if (position == 0) GroupTabFragment.newInstance(groupId)
        else GroupTabFragment.newInstance(groupId, taggedTabIds[position - 1])

    override fun getItemCount(): Int = taggedTabIds.size + 1

    override fun getItemId(position: Int) =
        (if (position == 0) groupId else taggedTabIds[position - 1]).toLong()

    override fun containsItem(itemId: Long) =
        itemId.toString() == groupId || taggedTabIds.contains(itemId.toString())
}