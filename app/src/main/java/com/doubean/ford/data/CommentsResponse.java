package com.doubean.ford.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CommentsResponse {
    @SerializedName("popular_comments")
    private List<GroupTopicComment> popularComments;
    private int start;
    private int total;
    private int count;
    private List<GroupTopicComment> comments;

    public List<GroupTopicComment> getPopularComments() {
        return popularComments;
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

    public List<GroupTopicComment> getComments() {
        return comments;
    }

    public List<String> getPopularCommentIds() {
        List<String> comments = new ArrayList<>();
        for (GroupTopicComment item : popularComments) {
            comments.add(item.id);
        }
        return comments;
    }
}
