package com.doubean.ford.data;

import java.util.List;

public class GroupTopicComments {
    private List<GroupTopicComment> popularComments;
    private List<GroupTopicComment> allComments;

    public GroupTopicComments() {
    }

    public GroupTopicComments(List<GroupTopicComment> popularComments, List<GroupTopicComment> allComments) {
        this.popularComments = popularComments;
        this.allComments = allComments;
    }

    public List<GroupTopicComment> getPopularComments() {
        return popularComments;
    }

    public void setPopularComments(List<GroupTopicComment> popularComments) {
        this.popularComments = popularComments;
    }

    public List<GroupTopicComment> getAllComments() {
        return allComments;
    }

    public void setAllComments(List<GroupTopicComment> allComments) {
        this.allComments = allComments;
    }
}
