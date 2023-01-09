package com.doubean.ford.data.vo;

import java.util.List;

public class PostComments {
    private List<PostComment> topComments;
    private List<PostComment> allComments;

    public PostComments() {
    }

    public PostComments(List<PostComment> topComments, List<PostComment> allComments) {
        this.topComments = topComments;
        this.allComments = allComments;
    }

    public List<PostComment> getTopComments() {
        return topComments;
    }

    public void setTopComments(List<PostComment> topComments) {
        this.topComments = topComments;
    }

    public List<PostComment> getAllComments() {
        return allComments;
    }

    public void setAllComments(List<PostComment> allComments) {
        this.allComments = allComments;
    }
}
