package com.doubean.ford.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "group_topic_popular_comments")
public class GroupTopicPopularComments {
    @PrimaryKey
    @NonNull
    public String groupTopicId;
    public List<String> commentIds;

    public GroupTopicPopularComments(@NonNull String groupTopicId, List<String> commentIds) {
        this.groupTopicId = groupTopicId;
        this.commentIds = commentIds;
    }

    public void setCommentIds(List<String> commentIds) {
        this.commentIds = commentIds;
    }

}
