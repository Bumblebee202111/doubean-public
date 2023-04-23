package com.doubean.ford.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data class that represents a recommendation of groups response from Douban.
 */
public class RecommendedGroupsResponse {
    int count;
    String title;
    @SerializedName("groups")
    List<RecommendedGroupResponseItem> items;

    public List<RecommendedGroupResponseItem> getItems() {
        return items;
    }
}
