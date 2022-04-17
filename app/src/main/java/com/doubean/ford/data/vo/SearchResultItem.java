package com.doubean.ford.data.vo;

import com.google.gson.annotations.SerializedName;

public class SearchResultItem {
    @SerializedName("target")
    private GroupItem group;

    public GroupItem getGroup() {
        return group;
    }
}
