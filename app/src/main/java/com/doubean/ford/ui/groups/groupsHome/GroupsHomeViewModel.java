package com.doubean.ford.ui.groups.groupsHome;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.GroupFollowItem;
import com.doubean.ford.data.vo.Resource;
import com.doubean.ford.util.LiveDataUtil;

import java.util.List;

public class GroupsHomeViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private final GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository;
    private final LiveData<List<GroupFollowItem>> followList;


    public GroupsHomeViewModel(@NonNull GroupRepository groupRepository, @NonNull GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository) {
        super();
        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository;
        this.followList = createFollowListLiveData();
        this.groupRepository = groupRepository;

    }

    public LiveData<List<GroupFollowItem>> getFollowList() {
        return followList;
    }

    private LiveData<List<GroupFollowItem>> createFollowListLiveData() {
        return Transformations.switchMap(groupsFollowsAndSavesRepository.getFollowIds(), follows -> {
            LiveData<GroupFollowItem>[] array = new LiveData[follows.size()];
            for (int i = 0; i < array.length; i++) {
                LiveData<Resource<GroupDetail>> group = groupRepository.getGroup(follows.get(i).groupId, false);
                int finalI = i;
                array[i] = Transformations.map(group, g -> {
                    if (g != null && g.data != null) {
                        return new GroupFollowItem(follows.get(finalI).groupId, follows.get(finalI).groupTabId, g.data);
                    }
                    return null;
                });
            }
            return LiveDataUtil.zip(array);
        });

    }


}