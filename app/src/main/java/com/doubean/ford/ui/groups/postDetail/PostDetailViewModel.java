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
            public LiveData<Resource<Boolean>> loadNextPageFromRepo(String... params) {
                return groupRepository.getNextPagePostComments(params[0]);
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

    /*
    @VisibleForTesting
    static class NextPageHandler implements Observer<Resource<Boolean>> {
        private final MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();
        private final GroupRepository repository;
        @VisibleForTesting
        boolean hasMore;
        @Nullable
        private LiveData<Resource<Boolean>> nextPageLiveData;

        @VisibleForTesting
        NextPageHandler(GroupRepository repository) {
            this.repository = repository;
            reset();
        }

        void loadNextPage(String postId) {
            unregister();
            nextPageLiveData = repository.getNextPagePostComments(postId);
            loadMoreState.setValue(new LoadMoreState(true, null));
            //noinspection ConstantConditions
            nextPageLiveData.observeForever(this);
        }

        @Override
        public void onChanged(@Nullable Resource<Boolean> result) {
            if (result == null) {
                reset();
            } else {
                switch (result.status) {
                    case SUCCESS:
                        hasMore = Boolean.TRUE.equals(result.data);
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false, null));
                        break;
                    case ERROR:
                        hasMore = true;
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false,
                                result.message));
                        break;
                }
            }
        }

        private void unregister() {
            if (nextPageLiveData != null) {
                nextPageLiveData.removeObserver(this);
                nextPageLiveData = null;

            }
        }

        private void reset() {
            unregister();
            hasMore = true;
            loadMoreState.setValue(new LoadMoreState(false, null));
        }

        MutableLiveData<LoadMoreState> getLoadMoreState() {
            return loadMoreState;
        }
    }

     */
}