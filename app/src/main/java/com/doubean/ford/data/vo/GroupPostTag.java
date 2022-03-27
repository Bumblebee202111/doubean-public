package com.doubean.ford.data.vo;

import androidx.annotation.Nullable;


public class GroupPostTag {

    public static final GroupPostTag DEFAULT = new GroupPostTag(null, null);
    public String id;
    public String name;

    public GroupPostTag(@Nullable String id, String name) {
        this.id = id;
        this.name = name;
    }
}
