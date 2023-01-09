package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.doubean.ford.data.db.Converters;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

@Entity(tableName = "post_comments")
@TypeConverters(Converters.class)
public class PostComment extends Item {
    @PrimaryKey
    @NonNull
    public String id;

    public User author;

    @ColumnInfo(name = "post_id")
    public String postId;

    public List<SizedPhoto> photos;

    public String text;

    @SerializedName("create_time")
    public LocalDateTime created;

    @SerializedName("vote_count")
    public int voteCount;

    @SerializedName("ref_comment")
    public PostComment repliedTo;

    @Override
    public String getId() {
        return id;
    }

}
