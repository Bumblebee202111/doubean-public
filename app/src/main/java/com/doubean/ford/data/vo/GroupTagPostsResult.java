package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

import java.util.List;

@Entity(tableName = "group_tag_posts_results", primaryKeys = {"tagId", "sortBy"})
public class GroupTagPostsResult extends ListResult {
    @NonNull
    public String groupId;

    @NonNull
    public String tagId;

    @NonNull
    public PostSortBy sortBy;

    public GroupTagPostsResult(@NonNull String groupId, @NonNull String tagId, @NonNull PostSortBy sortBy, @Nullable List<String> ids, int totalCount, @Nullable Integer next) {
        super(ids, totalCount, next);
        this.groupId = groupId;
        this.tagId = tagId;
        this.sortBy = sortBy;
    }

}
