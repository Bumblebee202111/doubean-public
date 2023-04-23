package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "group_search_results")
public class GroupSearchResult extends ListResult {
    @PrimaryKey
    @NonNull
    public String query;

    public GroupSearchResult(@NonNull String query, @Nullable List<String> ids, int totalCount,
                             @Nullable Integer next) {
        super(ids, totalCount, next);
        this.query = query;
    }


}
