package com.doubean.ford.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GroupTopicTag {
    @PrimaryKey
    public int Id;
    public String Name;
}
