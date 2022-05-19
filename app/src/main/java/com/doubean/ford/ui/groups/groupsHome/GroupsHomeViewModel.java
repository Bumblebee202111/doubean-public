package com.doubean.ford.ui.groups.groupsHome;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowedAndSavedRepository;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.GroupFollowed;
import com.doubean.ford.data.vo.GroupFollowedItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupsHomeViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private final GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository;
    private final LiveData<List<GroupFollowedItem>> followedList;


    public GroupsHomeViewModel(@NonNull GroupRepository groupRepository, @NonNull GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository) {
        super();
        this.groupsFollowedAndSavedRepository = groupsFollowedAndSavedRepository;
        this.followedList = createFollowedListLiveData();
        this.groupRepository = groupRepository;

    }

    public LiveData<List<GroupFollowedItem>> getFollowedList() {
        return followedList;
    }

    private LiveData<List<GroupFollowedItem>> createFollowedListLiveData() {
        MediatorLiveData<List<GroupFollowedItem>> followedListLiveData = new MediatorLiveData<>();
        followedListLiveData.addSource(groupsFollowedAndSavedRepository.getFollowedIds(), new Observer<List<GroupFollowed>>() {
            @Override
            public void onChanged(List<GroupFollowed> groupFollowedIds) {
                if (groupFollowedIds != null) {
                    List<GroupFollowedItem> followedList = new ArrayList<>(Arrays.asList(new GroupFollowedItem[groupFollowedIds.size()]));
                    for (int i = 0; i < groupFollowedIds.size(); i++) {
                        LiveData<GroupDetail> groupLiveData = groupRepository.getGroup(groupFollowedIds.get(i).groupId, false);
                        int finalI = i;
                        followedListLiveData.addSource(groupLiveData, new Observer<GroupDetail>() {
                            @Override
                            public void onChanged(GroupDetail group) {
                                if (group != null) {
                                    GroupFollowedItem groupFollowedItem = new GroupFollowedItem(group, groupFollowedIds.get(finalI).groupTabId);
                                    followedList.set(finalI, groupFollowedItem);
                                    boolean loaded = true;
                                    for (GroupFollowedItem followedItem : followedList) {
                                        if (followedItem == null) {
                                            loaded = false;
                                            break;
                                        }
                                    }
                                    if (loaded)
                                        followedListLiveData.postValue(followedList);
                                    followedListLiveData.removeSource(groupLiveData);
                                }

                            }
                        });
                    }
                }
            }
        });
        return followedListLiveData;
    }

    private LiveData<List<GroupFollowedItem>> createFollowedLiveData1() {
        MediatorLiveData<List<GroupFollowedItem>> followedListLiveData = new MediatorLiveData<>();

        followedListLiveData.addSource(groupsFollowedAndSavedRepository.getFollowedIds(), followedIds -> {
            if (followedIds != null) {
                List<LiveData<GroupFollowedItem>> followedLiveDataList = new ArrayList<>();
                for (int i = 0; i < followedIds.size(); i++)
                    followedLiveDataList.add(new MutableLiveData<>());

                for (int i = 1; i < followedLiveDataList.size(); i++) {
                    int index = i;
                    followedLiveDataList.set(i, Transformations.switchMap(followedLiveDataList.get(i - 1), new Function<GroupFollowedItem, LiveData<GroupFollowedItem>>() {
                        @Override
                        public LiveData<GroupFollowedItem> apply(GroupFollowedItem followedItem) {
                            LiveData<GroupFollowedItem> followedDetail = null;
                            if (followedItem != null) {
                                followedDetail = getGroupFollowed(followedIds.get(index));
                            }
                            return followedDetail;
                        }
                    }));
                }
                followedLiveDataList.set(0, getGroupFollowed(followedIds.get(0)));
                followedListLiveData.addSource(followedLiveDataList.get(followedLiveDataList.size() - 1), new Observer<GroupFollowedItem>() {
                    @Override
                    public void onChanged(GroupFollowedItem groupFollowedItem) {
                        if (groupFollowedItem != null) {
                            List<GroupFollowedItem> followedItemList = new ArrayList<>();
                            for (LiveData<GroupFollowedItem> followedItem : followedLiveDataList) {
                                followedItemList.add(followedItem.getValue());
                            }
                            followedListLiveData.setValue(followedItemList);
                        }

                    }
                });
            }


        });

        return followedListLiveData;
    }

    private LiveData<GroupFollowedItem> getGroupFollowed(GroupFollowed groupFollowed) {
        LiveData<GroupDetail> groupLiveData = groupRepository.getGroup(groupFollowed.groupId, false);
        return Transformations.map(groupLiveData,
                group -> new GroupFollowedItem(group, groupFollowed.groupTabId));
    }

}