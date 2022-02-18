package com.doubean.ford.data;

import java.util.List;

public class GroupTopicsResponse {
    private int count;
    private int start;
    private int total;
    private List<GroupTopic> topics;

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

    public List<GroupTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<GroupTopic> topics) {
        this.topics = topics;
    }

}
