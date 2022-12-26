package com.doubean.ford.data.vo;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.doubean.ford.data.db.Converters;

import java.util.List;

@Entity
@TypeConverters(Converters.class)
public class ListResult {
    public final int totalCount;
    @Nullable
    public final Integer next;
    @Nullable
    public List<String> ids;

    public ListResult(@Nullable List<String> ids, int totalCount, @Nullable Integer next) {
        this.ids = ids;
        this.totalCount = totalCount;
        this.next = next;
    }

}
