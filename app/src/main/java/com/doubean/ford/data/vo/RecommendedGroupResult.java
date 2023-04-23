package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recommended_groups_results")
public class RecommendedGroupResult {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @NonNull
    public Integer no;
    @NonNull
    public String groupId;
    @Nullable
    public String postId;

    public RecommendedGroupResult(@NonNull Integer no, @NonNull String groupId, @Nullable String postId) {
        this.no = no;
        this.groupId = groupId;
        this.postId = postId;
    }
}
