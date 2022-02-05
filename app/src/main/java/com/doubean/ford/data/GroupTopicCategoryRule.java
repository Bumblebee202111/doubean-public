package com.doubean.ford.data;

import androidx.room.Entity;

import java.util.List;

@Entity
public class GroupTopicCategoryRule {
    int id;
    List<Integer> groupIds;
    List<Integer> groupTopicTagId;
    String regex;
    String notRegex;
    boolean strict;
}
