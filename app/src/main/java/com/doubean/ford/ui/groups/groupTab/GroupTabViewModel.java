package com.doubean.ford.ui.groups.groupTab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository;
import com.doubean.ford.data.vo.PostItem;
import com.doubean.ford.data.vo.Resource;
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
    private final GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository;
    private final MutableLiveData<Boolean> reloadTrigger = new MutableLiveData<>();

    public GroupTabViewModel(GroupRepository groupRepository, GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository, String groupId, String tagId) {
        nextPageHandler = new NextPageHandler() {
            @Override
            public LiveData<Resource<Boolean>> loadNextPageFromRepo(String... params) {
                return params[1] == null ? repository.getNextPageGroupPosts(params[0]) : repository.getNextPageGroupTagPosts(params[0], params[1]);
            }
        };
        this.repository = groupRepository;
        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository;
        this.groupId = groupId;
        this.tagId = tagId;
        posts = Transformations.switchMap(reloadTrigger, i -> tagId == null ? repository.getGroupPosts(groupId) : repository.getGroupTagPosts(groupId, tagId));
        this.followed = groupsFollowsAndSavesRepository.getFollowed(groupId, tagId);
        refreshPosts();
    }

    public void refreshPosts() {
        reloadTrigger.setValue(true);
    }

    public LiveData<Resource<List<PostItem>>> getPosts() {
        return posts;
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
        nextPageHandler.loadNextPage(groupId, tagId);
    }

}