package com.doubean.ford.api;

import com.doubean.ford.data.vo.GroupPostItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupPostsResponse {
    private int count;
    private int start;
    private int total;
    @SerializedName("topics")
    private List<GroupPostItem> posts;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public List<GroupPostItem> getPosts() {
        return posts;
    }

    public void setPosts(List<GroupPostItem> posts) {
        this.posts = posts;
    }

}
