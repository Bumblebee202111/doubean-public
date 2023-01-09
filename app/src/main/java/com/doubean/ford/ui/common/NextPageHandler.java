package com.doubean.ford.ui.common;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.doubean.ford.data.vo.Resource;

import java.util.Arrays;

public abstract class NextPageHandler implements Observer<Resource<Boolean>> {
    private final MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();
    boolean hasMore;
    private String[] params;
    @Nullable
    private LiveData<Resource<Boolean>> nextPageLiveData;

    public NextPageHandler() {
        reset();
    }

    public abstract LiveData<Resource<Boolean>> loadNextPageFromRepo(String... params);

    public void loadNextPage(String... params) {
        if (Arrays.equals(this.params, params)) {
            return;
        }
        this.params = params;
        unregister();
        nextPageLiveData = loadNextPageFromRepo(params);
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
            if (hasMore) {
                params = null;
            }
        }
    }

    public void reset() {
        unregister();
        hasMore = true;
        loadMoreState.setValue(new LoadMoreState(false, null));
    }

    public MutableLiveData<LoadMoreState> getLoadMoreState() {
        return loadMoreState;
    }
}