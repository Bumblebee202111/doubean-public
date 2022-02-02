package com.doubean.ford.ui.groups;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.FavGroup;
import com.doubean.ford.data.Group;
import com.doubean.ford.data.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;

public class GroupsViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    LiveData<List<FavGroup>> favGroupList;
    private LiveData<List<Group>> favGroups;

    public GroupsViewModel(@NonNull GroupRepository groupRepository) {
        super();
        this.favGroupList = groupRepository.getFavGroups();
        this.favGroups = Transformations.switchMap(favGroupList, new Function<List<FavGroup>, LiveData<List<Group>>>() {
            @Override
            public LiveData<List<Group>> apply(List<FavGroup> input) {
                List<String> ids = new ArrayList<>();
                for (FavGroup g : input) {
                    ids.add(g.groupId);
                }
                return groupRepository.getGroups(ids);
            }
        });
        this.groupRepository = groupRepository;

    }

    public LiveData<List<Group>> getFavGroups() {
        return favGroups;
    }

    public LiveData<List<FavGroup>> getFavGroupList() {
        return favGroupList;
    }

    ;

    public void addFavGroup(@NonNull FavGroup favGroup) {
        groupRepository.addFavGroup(favGroup);

    }

}