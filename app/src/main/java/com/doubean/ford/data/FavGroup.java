package com.doubean.ford.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "fav_groups")
@ForeignKey(entity = Group.class, parentColumns = {"id"}, childColumns = {"group_id"})
public class FavGroup {
    @PrimaryKey
    @NonNull
    public String groupId;

    public FavGroup(@NonNull String groupId) {
        this.groupId = groupId;
    }
}
