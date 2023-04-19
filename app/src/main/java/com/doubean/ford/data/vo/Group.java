package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

@Entity(tableName = "groups")
public class Group {

    @PrimaryKey
    @NonNull
    public String id;

    @NonNull
    public String name;

    @SerializedName("member_count")
    public Integer memberCount;

    @SerializedName("topic_count")
    public Integer postCount;

    @SerializedName("create_time")
    public LocalDateTime dateCreated;

    @SerializedName("sharing_url")
    public String url;

    public String uri;

    @SerializedName("avatar")
    public String avatarUrl;

    @SerializedName("member_name")
    public String memberName;

    @SerializedName("desc_abstract")
    public String shortDescription;

    @SerializedName("desc")
    public String description;

    @SerializedName("group_tabs")
    public List<GroupTab> tabs;

    @SerializedName("post_tags_normal")
    public List<GroupPostTag> postTags;

    @SerializedName("background_mask_color")
    public String colorString;

    public Group() {
    }


}
