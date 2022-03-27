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
public class GroupSearchResult {
    @PrimaryKey
    @NonNull
    public String query;
    @Nullable
    public List<String> groupIds;

    public GroupSearchResult(@NonNull String query, @Nullable List<String> groupIds) {
        this.query = query;
        this.groupIds = groupIds;
    }
}
