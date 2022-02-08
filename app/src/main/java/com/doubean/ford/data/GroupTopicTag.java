package com.doubean.ford.data;

import androidx.annotation.Nullable;


public class GroupTopicTag {

    public static final GroupTopicTag DEFAULT = new GroupTopicTag(null, "全部");
    public String id;
    public String name;

    public GroupTopicTag(@Nullable String id, String name) {
        this.id = id;
        this.name = name;
    }
}
