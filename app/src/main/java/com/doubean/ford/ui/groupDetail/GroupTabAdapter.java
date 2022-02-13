package com.doubean.ford.ui.groupDetail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.doubean.ford.data.GroupTopicTag;
import com.doubean.ford.ui.groupTab.GroupTabFragment;

import java.util.List;

public class GroupTabAdapter extends FragmentStateAdapter {
    private String groupId;
    private List<GroupTopicTag> groupTopicTags;

    public GroupTabAdapter(Fragment fragment, String groupId, List<GroupTopicTag> groupTopicTags) {
        super(fragment);
        this.groupId = groupId;
        this.groupTopicTags = groupTopicTags;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new GroupTabFragment();
        Bundle args = new Bundle();
        args.putString(GroupTabFragment.ARG_GROUP_ID, groupId);
        if (position > 0)
            args.putString(GroupTabFragment.ARG_TAG_ID, groupTopicTags.get(position - 1).id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return groupTopicTags.size() + 1;
    }
}