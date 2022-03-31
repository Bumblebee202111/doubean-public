package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class also known as topic/话题
 */
@Entity(tableName = "group_posts")
public class GroupPost {
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    public String id;
    @ColumnInfo(name = "group_id")
    public String groupId;
    @ColumnInfo(name = "tag_id")
    public String tagId;
    public String title;
    public User author;
    @SerializedName("create_time")
    @ColumnInfo(name = "date_created")
    public LocalDateTime dateCreated;
    @SerializedName("update_time")
    @ColumnInfo(name = "date_updated")
    public LocalDateTime dateUpdated;
    @SerializedName("like_count")
    @ColumnInfo(name = "like_count")
    public int likeCount;
    @SerializedName("comments_count")
    @ColumnInfo(name = "comment_count")
    public int commentCount;
    public String content;
    @SerializedName("topic_tags")
    public List<GroupPostTag> postTags;
    @SerializedName("cover_url")
    @ColumnInfo(name = "cover_url")
    public String coverUrl;
    public String url;
    public Group group;


    public String getTagName() {
        if (postTags.isEmpty()) return null;
        return postTags.get(0).name;
    }
}
