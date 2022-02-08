package com.doubean.ford.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;


@Entity(tableName = "group_topics")
public class GroupTopic {
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    public String topicId;
    @ColumnInfo(name = "group_id")
    public String groupId;
    @ColumnInfo(name = "tag_id")
    public String tagId;
    public String title;
    //public int author;
    @SerializedName("create_time")
    @ColumnInfo(name = "date_created")
    public Date dateCreated;
    @SerializedName("update_time")
    @ColumnInfo(name = "date_updated")
    public Date dateUpdated;
    @SerializedName("like_count")
    @ColumnInfo(name = "like_count")
    public int likeCount;
    @SerializedName("comments_count")
    @ColumnInfo(name = "comment_count")
    public int commentCount;

    @SerializedName("topic_tags")
    public List<GroupTopicTag> topicTags;
    @SerializedName("cover_url")
    @ColumnInfo(name = "cover_url")
    public String coverUrl;
    public String url;

    public GroupTopic(@NonNull String topicId, String groupId, String tagId, String title, Date dateCreated, Date dateUpdated, int likeCount, int commentCount, List<GroupTopicTag> topicTags, String coverUrl) {
        this.topicId = topicId;
        this.groupId = groupId;
        this.tagId = tagId;
        this.title = title;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.topicTags = topicTags;
        this.coverUrl = coverUrl;
    }
}
