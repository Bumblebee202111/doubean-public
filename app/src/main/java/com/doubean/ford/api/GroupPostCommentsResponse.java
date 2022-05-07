package com.doubean.ford.api;

import com.doubean.ford.data.vo.GroupPostComment;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GroupPostCommentsResponse {
    @SerializedName("popular_comments")
    private List<GroupPostComment> topComments;
    private int start;
    private int total;
    private int count;
    private List<GroupPostComment> comments;

    public List<GroupPostComment> getTopComments() {
        return topComments;
    }

    public int getStart() {
        return start;
    }

    public int getTotal() {
        return total;
    }

    public int getCount() {
        return count;
    }

    public List<GroupPostComment> getComments() {
        return comments;
    }

    public List<String> getTopCommentIds() {
        List<String> comments = new ArrayList<>();
        for (GroupPostComment item : topComments) {
            comments.add(item.id);
        }
        return comments;
    }
}
