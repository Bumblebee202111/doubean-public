package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "group_post_popular_comments")
public class GroupPostPopularComments {
    @PrimaryKey
    @NonNull
    public String groupPostId;
    public List<String> commentIds;

    public GroupPostPopularComments(@NonNull String groupPostId, List<String> commentIds) {
        this.groupPostId = groupPostId;
        this.commentIds = commentIds;
    }

    public void setCommentIds(List<String> commentIds) {
        this.commentIds = commentIds;
    }

}
