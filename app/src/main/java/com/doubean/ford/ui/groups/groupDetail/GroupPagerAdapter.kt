package com.doubean.ford.ui.groups.groupDetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.doubean.ford.data.vo.GroupDetail
import com.doubean.ford.ui.groups.groupTab.GroupTabFragment

class GroupPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle?,
    private val group: GroupDetail?
) : FragmentStateAdapter(
    fragmentManager, lifecycle!!
) {
    override fun createFragment(position: Int): Fragment {
        var tagId: String? = null
        if (position > 0) tagId = group!!.tabs!![position - 1].id
        return GroupTabFragment.newInstance(group!!.id, tagId, group)
    }

    override fun getItemCount(): Int {
        return if (group == null) 0 else group.tabs!!.size + 1
    }
}