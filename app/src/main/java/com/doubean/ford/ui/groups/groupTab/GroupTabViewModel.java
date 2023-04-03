package com.doubean.ford.ui.groups.groupTab;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.PostItem;
import com.doubean.ford.data.vo.Resource;
import com.doubean.ford.data.vo.SortBy;
import com.doubean.ford.ui.common.LoadMoreState;
import com.doubean.ford.ui.common.NextPageHandler;

import java.util.List;

/**
 * Make LiveData refreshable:
 * https://gist.github.com/ivanalvarado/726a6c3f5ffad54958fe4670269bd897
 */
public class GroupTabViewModel extends ViewModel {
    private final LiveData<Boolean> followed;
    private final NextPageHandler nextPageHandler;
    private final GroupRepository repository;
    private final String groupId;
    private final String tagId;
    private final LiveData<Resource<List<PostItem>>> posts;
    private final MutableLiveData<SortBy> sortBy = new MutableLiveData<>();
    private final LiveData<Resource<GroupDetail>> group;
    private final GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository;
    private final MutableLiveData<Boolean> reloadTrigger = new MutableLiveData<>();

    public GroupTabViewModel(GroupRepository groupRepository, GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository, String groupId, String tagId) {
        nextPageHandler = new NextPageHandler() {
            @Override
            public LiveData<Resource<Boolean>> loadNextPageFromRepo(Object... params) {
                return params[1] == null ? repository.getNextPageGroupPosts((String) params[0], (SortBy) params[2]) : repository.getNextPageGroupTagPosts((String) params[0], (String) params[1], (SortBy) params[2]);
            }
        };
        this.repository = groupRepository;
        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository;
        this.groupId = groupId;
        this.group = repository.getGroup(groupId, false);
        this.tagId = tagId;
        posts = Transformations.switchMap(reloadTrigger, rt -> Transformations.switchMap(sortBy, new Function<SortBy, LiveData<Resource<List<PostItem>>>>() {
            @Override
            public LiveData<Resource<List<PostItem>>> apply(SortBy type) {
                return tagId == null ? repository.getGroupPosts(groupId, type) : repository.getGroupTagPosts(groupId, tagId, type);
            }
        }));
        this.followed = groupsFollowsAndSavesRepository.getFollowed(groupId, tagId);
        refreshPosts();
    }

    public void setSortBy(SortBy sortBy) {
        nextPageHandler.reset();
        if (sortBy == this.sortBy.getValue()) {
            return;
        }
        this.sortBy.setValue(sortBy);
    }

    public void refreshPosts() {
        reloadTrigger.setValue(true);
    }

    public LiveData<Resource<List<PostItem>>> getPosts() {
        return posts;
    }

    public LiveData<Resource<GroupDetail>> getGroup() {
        return group;
    }

    public LiveData<Boolean> getFollowed() {
        return followed;
    }

    public void addFollow() {
        groupsFollowsAndSavesRepository.createFollow(groupId, tagId);
    }

    public void removeFollow() {
        groupsFollowsAndSavesRepository.removeFollow(groupId, tagId);
    }

    public LiveData<LoadMoreState> getLoadMoreStatus() {
        return nextPageHandler.getLoadMoreState();
    }


    public void loadNextPage() {
        nextPageHandler.loadNextPage(groupId, tagId, sortBy.getValue());
    }

}