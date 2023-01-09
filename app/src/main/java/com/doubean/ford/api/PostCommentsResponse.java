package com.doubean.ford.api;

import com.doubean.ford.data.vo.PostComment;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PostCommentsResponse extends ListResponse<PostComment> {
    @SerializedName("popular_comments")
    private List<PostComment> topComments;

    public PostCommentsResponse(int start, int count, int total, List<PostComment> items) {
        super(start, count, total, items);
    }

    public List<PostComment> getTopComments() {
        return topComments;
    }


    public List<String> getTopCommentIds() {
        List<String> comments = new ArrayList<>();
        for (PostComment item : topComments) {
            comments.add(item.id);
        }
        return comments;
    }

}
