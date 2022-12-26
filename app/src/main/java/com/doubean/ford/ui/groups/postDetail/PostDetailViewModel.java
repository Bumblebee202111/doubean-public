package com.doubean.ford.ui.groups.postDetail;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.vo.GroupPost;
import com.doubean.ford.data.vo.GroupPostComments;
import com.doubean.ford.data.vo.Resource;

public class PostDetailViewModel extends ViewModel {
    String postId;
    private GroupRepository groupRepository;
    private LiveData<Resource<GroupPost>> post;
    private LiveData<Resource<GroupPostComments>> groupPostComments;

    public PostDetailViewModel(GroupRepository groupRepository, String postId) {
        this.groupRepository = groupRepository;
        this.postId = postId;
        this.post = groupRepository.getGroupPost(postId);
        this.groupPostComments = groupRepository.getGroupPostComments(postId);
    }

    public LiveData<Resource<GroupPost>> getPost() {
        return post;
    }

    public LiveData<Resource<GroupPostComments>> getPostComments() {
        return groupPostComments;
    }
}