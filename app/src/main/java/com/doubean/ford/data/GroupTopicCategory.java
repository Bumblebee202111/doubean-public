package com.doubean.ford.data;

import androidx.annotation.Nullable;
import androidx.room.Entity;

import java.util.List;

@Entity
public class GroupTopicCategory {
    int id;
    String name;
    @Nullable
    String desc;
    List<GroupTopicCategoryRule> rules;
}
