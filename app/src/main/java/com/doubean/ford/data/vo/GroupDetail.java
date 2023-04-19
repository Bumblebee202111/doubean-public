package com.doubean.ford.data.vo;

import android.graphics.Color;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class GroupDetail implements Serializable {
    @NonNull
    public String id;

    @NonNull
    public String name;

    @SerializedName("member_count")
    public int memberCount;

    @SerializedName("topic_count")
    public int postCount;

    @SerializedName("create_time")
    public LocalDateTime dateCreated;

    @SerializedName("sharing_url")
    public String url;

    @SerializedName("avatar")
    public String avatarUrl;

    @SerializedName("member_name")
    public String memberName;

    @SerializedName("desc")
    public String description;

    @SerializedName("group_tabs")
    public List<GroupTab> tabs;

    @SerializedName("background_mask_color")
    public String colorString;

    public String uri;

    @Ignore
    public int getColor() {
        return Color.parseColor(TextUtils.isEmpty(colorString) ? "#FFFFFF" : colorString);
    }

    @Ignore
    public GroupItem toGroupItem() {
        GroupItem groupItem = new GroupItem();
        groupItem.name = this.name;
        groupItem.url = this.url;
        groupItem.dateCreated = this.dateCreated;
        groupItem.avatarUrl = this.avatarUrl;
        groupItem.shortDescription = this.description;
        groupItem.id = this.id;
        groupItem.postCount = this.postCount;
        return groupItem;
    }
}
