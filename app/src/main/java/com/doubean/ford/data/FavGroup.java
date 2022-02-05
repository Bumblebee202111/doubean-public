package com.doubean.ford.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fav_groups")
public class FavGroup {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "group_id")
    public String groupId;

    public FavGroup(@NonNull String groupId) {
        this.groupId = groupId;
    }
}
