package com.doubean.ford.ui.groups.groupDetail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.ui.groups.groupTab.GroupTabFragment;

public class GroupPagerAdapter extends FragmentStateAdapter {
    private final GroupDetail group;

    public GroupPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, GroupDetail group) {
        super(fragmentManager, lifecycle);
        this.group = group;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String tagId = null;
        if (position > 0) tagId = group.tabs.get(position - 1).id;
        return GroupTabFragment.newInstance(group.id, tagId, group);
    }

    @Override
    public int getItemCount() {
        if (group == null) return 0;
        return group.tabs.size() + 1;
    }
}