package com.doubean.ford.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "groups")
public final class Group {

    @PrimaryKey
    private final int groupId;

    @NonNull
    private final String groupName;

    private final int memberCount;

    public Group(int groupId, @NonNull String groupName, int memberCount) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.memberCount = memberCount;
    }

    public int getGroupId() {
        return groupId;
    }

    @NonNull
    public String getGroupName() {
        return groupName;
    }

    public int getMemberCount() {
        return memberCount;
    }
}
