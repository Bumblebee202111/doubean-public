package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class officially known as topic/话题
 */
@Entity
public class Post {
    @NonNull
    @PrimaryKey
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
    public Integer likeCount;

    @SerializedName("reactions_count")
    public Integer reactionCount;

    @SerializedName("reshares_count")
    public Integer repostCount;

    @SerializedName("collections_count")
    public Integer saveCount;

    @SerializedName("comments_count")
    public Integer commentCount;

    @SerializedName("abstract")
    public String shortContent;

    public String content;

    @SerializedName("topic_tags")
    public List<GroupPostTag> postTags;

    @SerializedName("cover_url")
    public String coverUrl;

    public String url;

    public GroupBrief group;

    public String uri;

    public String getTagName() {
        if (postTags.isEmpty()) return null;
        return postTags.get(0).name;
    }
}
