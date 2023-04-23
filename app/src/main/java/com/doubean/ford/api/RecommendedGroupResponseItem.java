package com.doubean.ford.api;

import com.doubean.ford.data.vo.GroupItem;
import com.doubean.ford.data.vo.PostItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecommendedGroupResponseItem {

    private int trend;
    private GroupItem group;
    @SerializedName("topics")
    private List<PostItem> posts;

    public int getTrend() {
        return trend;
    }

    public void setTrend(int trend) {
        this.trend = trend;
    }

    public GroupItem getGroup() {
        return group;
    }

    public void setGroup(GroupItem group) {
        this.group = group;
    }

    public List<PostItem> getPosts() {
        return posts;
    }

    public void setPosts(List<PostItem> posts) {
        this.posts = posts;
    }

}
