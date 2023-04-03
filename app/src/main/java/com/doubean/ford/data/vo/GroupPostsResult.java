package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.doubean.ford.data.db.Converters;

import java.util.List;

@Entity(primaryKeys = {"groupId", "sortBy"})
@TypeConverters(Converters.class)
public class GroupPostsResult extends ListResult {
    @NonNull
    public String groupId;

    @NonNull
    public SortBy sortBy;

    public GroupPostsResult(@NonNull String groupId, @NonNull SortBy sortBy, @Nullable List<String> ids, int totalCount, @Nullable Integer next) {
        super(ids, totalCount, next);
        this.groupId = groupId;
        this.sortBy = sortBy;
    }

}
