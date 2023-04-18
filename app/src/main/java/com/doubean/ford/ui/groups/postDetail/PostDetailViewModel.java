package com.doubean.ford.ui.groups.postDetail;


import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.vo.Post;
import com.doubean.ford.data.vo.PostComments;
import com.doubean.ford.data.vo.Resource;
import com.doubean.ford.ui.common.LoadMoreState;
import com.doubean.ford.ui.common.NextPageHandler;

public class PostDetailViewModel extends ViewModel {
    private final NextPageHandler nextPageHandler;
    private final MutableLiveData<Boolean> reloadTrigger = new MutableLiveData<>();
    private final String postId;
    private final GroupRepository groupRepository;
    private final LiveData<Resource<Post>> post;
    private final LiveData<Resource<PostComments>> postComments;

    public PostDetailViewModel(GroupRepository groupRepository, String postId) {
        nextPageHandler = new NextPageHandler() {
            @Override
            public LiveData<Resource<Boolean>> loadNextPageFromRepo(Object... params) {
                return groupRepository.getNextPagePostComments((String) params[0]);
            }
        };
        this.groupRepository = groupRepository;
        this.postId = postId;
        post = groupRepository.getPost(postId);
        postComments = Transformations.switchMap(reloadTrigger, i -> groupRepository.getPostComments(postId));
        refreshPostComments();
    }

    public LiveData<Resource<Post>> getPost() {
        return post;
    }

    public LiveData<Resource<PostComments>> getPostComments() {
        return postComments;
    }

    public void refreshPostComments() {
        reloadTrigger.setValue(true);
    }

    @VisibleForTesting
    public LiveData<LoadMoreState> getLoadMoreStatus() {
        return nextPageHandler.getLoadMoreState();
    }

    @VisibleForTesting
    public void loadNextPage() {
        nextPageHandler.loadNextPage(postId);
    }

}