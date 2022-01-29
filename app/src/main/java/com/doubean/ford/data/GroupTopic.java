package com.doubean.ford.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity
public class GroupTopic {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    public String topicId;
    public String title;
    public int author;
    @ColumnInfo(name = "date_created")
    public Date dateCreated;
    @ColumnInfo(name = "date_updated")
    public Date dateUpdated;
    @ColumnInfo(name = "like_count")
    public int likeCount;
    @ColumnInfo(name = "comment_count")
    public int commentCount;


}
