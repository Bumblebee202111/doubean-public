package com.doubean.ford.ui.groups.groupDetail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.doubean.ford.data.vo.GroupPostTag;
import com.doubean.ford.ui.groups.groupTab.GroupTabFragment;

import java.util.ArrayList;
import java.util.List;

public class GroupPagerAdapter extends FragmentStateAdapter {
    private String groupId;
    private List<GroupPostTag> groupPostTags;

    public GroupPagerAdapter(Fragment fragment, String groupId, List<GroupPostTag> groupPostTags) {
        super(fragment);
        this.groupId = groupId;
        this.groupPostTags = groupPostTags == null ? new ArrayList<>() : groupPostTags;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new GroupTabFragment();
        Bundle args = new Bundle();
        args.putString(GroupTabFragment.ARG_GROUP_ID, groupId);
        if (position > 0)
            args.putString(GroupTabFragment.ARG_TAG_ID, groupPostTags.get(position - 1).id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return groupPostTags.size() + 1;
    }
}