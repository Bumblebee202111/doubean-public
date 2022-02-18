package com.doubean.ford.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "group_topic_comments")
public class GroupTopicComment {
    @PrimaryKey
    @NonNull
    public String id;
    public Author author;
    @ColumnInfo(name = "topic_id")
    public String topicId;
    public String text;
    @SerializedName("create_time")
    public Date createTime;
    @SerializedName("vote_count")
    public int voteCount;


}
