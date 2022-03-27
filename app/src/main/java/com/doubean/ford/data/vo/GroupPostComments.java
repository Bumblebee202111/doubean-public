package com.doubean.ford.data.vo;

import java.util.List;

public class GroupPostComments {
    private List<GroupPostComment> popularComments;
    private List<GroupPostComment> allComments;

    public GroupPostComments() {
    }

    public GroupPostComments(List<GroupPostComment> popularComments, List<GroupPostComment> allComments) {
        this.popularComments = popularComments;
        this.allComments = allComments;
    }

    public List<GroupPostComment> getPopularComments() {
        return popularComments;
    }

    public void setPopularComments(List<GroupPostComment> popularComments) {
        this.popularComments = popularComments;
    }

    public List<GroupPostComment> getAllComments() {
        return allComments;
    }

    public void setAllComments(List<GroupPostComment> allComments) {
        this.allComments = allComments;
    }
}
