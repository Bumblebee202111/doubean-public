package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "recommended_groups_result")
public class RecommendedGroupsResult {
    @NonNull
    public final List<Long> ids;

    @PrimaryKey
    @NonNull
    public GroupRecommendationType type;

    public RecommendedGroupsResult(@NonNull GroupRecommendationType type, @NonNull List<Long> ids) {
        this.type = type;
        this.ids = ids;
    }
}
