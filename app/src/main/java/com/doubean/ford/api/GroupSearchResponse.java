package com.doubean.ford.api;

import androidx.annotation.NonNull;

import com.doubean.ford.data.vo.SearchResultItem;

import java.util.ArrayList;
import java.util.List;
/**
 * Data class that represents a group search response from Douban.
 */
public class GroupSearchResponse extends ListResponse<SearchResultItem> {

    private String q;

    public GroupSearchResponse(int start, int count, int total, String q, List<SearchResultItem> items) {
        super(start, count, total, items);
        this.q = q;
    }

    @NonNull
    public List<String> getGroupIds() {
        List<String> groupIds = new ArrayList<>();
        for (SearchResultItem item : getItems()) {
            groupIds.add(item.getGroup().id);
        }
        return groupIds;
    }
}
