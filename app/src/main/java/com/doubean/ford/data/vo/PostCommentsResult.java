package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "post_comments_results")
public class PostCommentsResult extends ListResult {
    @PrimaryKey
    @NonNull
    public String postId;

    public PostCommentsResult(@NonNull String postId, @Nullable List<String> ids, int totalCount, @Nullable Integer next) {
        super(ids, totalCount, next);
        this.postId = postId;
    }
}
