package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.doubean.ford.data.db.Converters;

import java.util.List;

@Entity
@TypeConverters(Converters.class)
public class GroupTagPostsResult extends ListResult {
    @NonNull
    public String groupId;
    @PrimaryKey
    @NonNull
    public String tagId;

    public GroupTagPostsResult(@NonNull String groupId, @NonNull String tagId, @Nullable List<String> ids, int totalCount, @Nullable Integer next) {
        super(ids, totalCount, next);
        this.groupId = groupId;
        this.tagId = tagId;
    }

}
