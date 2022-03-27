package com.doubean.ford.data.vo;

import androidx.annotation.Nullable;
import androidx.room.Entity;

import java.util.List;

@Entity
public class GroupPostCategory {
    int id;
    String name;
    @Nullable
    String desc;
    List<GroupPostCategoryRule> rules;
}
