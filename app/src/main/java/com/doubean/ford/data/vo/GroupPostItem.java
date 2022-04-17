package com.doubean.ford.data.vo;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

public class GroupPostItem {

    @SerializedName("id")
    public String id;

    public String groupId;

    public String tagId;

    public String title;

    public User author;

    @SerializedName("create_time")
    public LocalDateTime created;

    @SerializedName("update_time")
    public LocalDateTime lastUpdated;

    @SerializedName("like_count")
    public int likeCount;

    @SerializedName("comments_count")
    public int commentCount;

    public String content;

    @SerializedName("topic_tags")
    public List<GroupPostTag> postTags;

    @SerializedName("cover_url")
    public String coverUrl;

    public String url;

    public Group group;

    public String getTagName() {
        if (postTags.isEmpty()) return null;
        return postTags.get(0).name;
    }
}
