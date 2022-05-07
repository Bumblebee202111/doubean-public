package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "group_post_top_comments")
public class GroupPostTopComments {
    @PrimaryKey
    @NonNull
    public String postId;
    public List<String> commentIds;

    public GroupPostTopComments(@NonNull String postId, List<String> commentIds) {
        this.postId = postId;
        this.commentIds = commentIds;
    }

    public void setCommentIds(List<String> commentIds) {
        this.commentIds = commentIds;
    }

}
