package com.doubean.ford.data.vo;

import com.google.gson.annotations.SerializedName;

public class SearchResultItem extends Item {
    @SerializedName("target")
    private GroupItem group;

    public GroupItem getGroup() {
        return group;
    }

    @Override
    public String getId() {
        return group.id;
    }
}
