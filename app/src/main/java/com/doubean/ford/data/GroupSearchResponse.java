package com.doubean.ford.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GroupSearchResponse {

    private String q;
    @SerializedName("total_count")
    private int total;
    @SerializedName("items")
    private List<SearchResultItem> items;

    public GroupSearchResponse(String q, List<SearchResultItem> items) {
        this.q = q;
        this.items = items;
    }

    public List<SearchResultItem> getItems() {
        return items;
    }

    @NonNull
    public List<String> getGroupIds() {
        List<String> groupIds = new ArrayList<>();
        for (SearchResultItem item : items) {
            groupIds.add(item.getGroup().id);
        }
        return groupIds;
    }
}
