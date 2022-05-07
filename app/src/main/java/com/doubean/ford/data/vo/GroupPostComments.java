package com.doubean.ford.data.vo;

import java.util.List;

public class GroupPostComments {
    private List<GroupPostComment> topComments;
    private List<GroupPostComment> allComments;

    public GroupPostComments() {
    }

    public GroupPostComments(List<GroupPostComment> topComments, List<GroupPostComment> allComments) {
        this.topComments = topComments;
        this.allComments = allComments;
    }

    public List<GroupPostComment> getTopComments() {
        return topComments;
    }

    public void setTopComments(List<GroupPostComment> topComments) {
        this.topComments = topComments;
    }

    public List<GroupPostComment> getAllComments() {
        return allComments;
    }

    public void setAllComments(List<GroupPostComment> allComments) {
        this.allComments = allComments;
    }
}
