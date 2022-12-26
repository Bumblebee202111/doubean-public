package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_follows", indices = @Index(value = {"group_id", "group_tab_id"}, unique = true))
public class GroupFollow {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    @ColumnInfo(name = "group_id")
    public String groupId;
    @Nullable
    @ColumnInfo(name = "group_tab_id")
    public String groupTabId;

    public GroupFollow(@NonNull String groupId, @Nullable String groupTabId) {
        this.groupId = groupId;
        this.groupTabId = groupTabId;
    }

    public boolean isGroup() {
        return groupTabId == null;
    }

    public boolean isGroupTab() {
        return groupTabId != null;
    }
}
