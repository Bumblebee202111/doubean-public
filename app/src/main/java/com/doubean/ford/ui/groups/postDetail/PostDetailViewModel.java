package com.doubean.ford.ui.groups.postDetail;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.vo.GroupPost;
import com.doubean.ford.data.vo.GroupPostComments;

public class PostDetailViewModel extends ViewModel {
    String postId;
    private GroupRepository groupRepository;
    private LiveData<GroupPost> groupPost;
    private LiveData<GroupPostComments> groupPostComments;

    public PostDetailViewModel(GroupRepository groupRepository, String postId) {
        this.groupRepository = groupRepository;
        this.postId = postId;
        this.groupPost = groupRepository.getGroupPost(postId);
        this.groupPostComments = groupRepository.getGroupPostComments(postId);
    }

    public LiveData<GroupPost> getPost() {
        return groupPost;
    }

    public LiveData<GroupPostComments> getPostComments() {
        return groupPostComments;
    }
}