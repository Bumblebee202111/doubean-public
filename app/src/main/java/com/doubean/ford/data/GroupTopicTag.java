package com.doubean.ford.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GroupTopicTag {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int tagId;
    @ColumnInfo(name = "name")
    public String tagName;
}
