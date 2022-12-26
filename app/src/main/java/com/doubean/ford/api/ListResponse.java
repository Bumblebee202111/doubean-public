package com.doubean.ford.api;

import androidx.annotation.NonNull;

import com.doubean.ford.data.vo.Item;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListResponse<ItemType extends Item> {

    private int start;
    @SerializedName("total_count")
    private int total;
    @SerializedName("items")
    private List<ItemType> items;

    public ListResponse(int start, List<ItemType> items) {
        this.start = start;
        this.items = items;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ItemType> getItems() {
        return items;
    }

    public void setItems(List<ItemType> items) {
        this.items = items;
    }

    @NonNull
    public List<String> getIds() {
        List<String> groupIds = new ArrayList<>();
        for (ItemType item : items) {
            groupIds.add(item.getId());
        }
        return groupIds;
    }

    public Integer getNextPageStart() {
        int next = start + items.size() + 1;
        return next > total ? null : next;
    }


}
