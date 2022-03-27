package com.doubean.ford.data.vo;

import com.google.gson.annotations.SerializedName;

public class SearchResultItem {
    @SerializedName("target")
    private Group group;

    public Group getGroup() {
        return group;
    }

}
