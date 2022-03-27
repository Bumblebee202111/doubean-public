package com.doubean.ford.data.vo;

import androidx.room.Entity;

import java.util.List;

@Entity
public class GroupPostCategoryRule {
    int id;
    List<Integer> groupIds;
    List<Integer> groupPostTagId;
    String regex;
    String notRegex;
    boolean strict;
}
