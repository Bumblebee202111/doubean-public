package com.doubean.ford.ui.groups.postDetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;

public class PostDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private String postId;

    public PostDetailViewModelFactory(@NonNull GroupRepository repository, String postId) {
        super();
        this.repository = repository;
        this.postId = postId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PostDetailViewModel(repository, postId);
    }
}