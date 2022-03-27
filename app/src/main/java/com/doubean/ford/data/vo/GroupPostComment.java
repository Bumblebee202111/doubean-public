package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@Entity(tableName = "group_post_comments")
public class GroupPostComment {
    @PrimaryKey
    @NonNull
    public String id;
    public User author;
    @ColumnInfo(name = "post_id")
    public String postId;
    public List<SizedPhoto> photos;

    public String text;
    @SerializedName("create_time")
    @ColumnInfo(name = "created")
    public Date created;

    @SerializedName("vote_count")
    public int voteCount;


}
