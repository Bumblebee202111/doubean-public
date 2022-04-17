package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GroupFavoriteDetail {

    private final String groupMemberName;
    private final int groupMemberCount;
    @NonNull
    private String groupId;
    private String groupName;
    private String groupAvatarUrl;
    private String groupTabName;
    private String groupColor;
    @Nullable
    private String groupTabId;

    public GroupFavoriteDetail(@NonNull GroupDetail group, @Nullable String groupTabId) {
        this.groupId = group.id;
        this.groupName = group.name;
        this.groupAvatarUrl = group.avatarUrl;
        this.groupTabId = groupTabId;
        if (groupTabId != null) {
            for (GroupPostTag tab : group.tabs) {
                if (tab.id.equals(groupTabId)) {
                    groupTabName = tab.name;
                }
            }
        }
        this.groupColor = group.colorString;
        this.groupMemberCount = group.memberCount;
        this.groupMemberName = group.memberName;
    }

    @NonNull
    public String getGroupId() {
        return groupId;
    }

    public String getGroupAvatarUrl() {
        return groupAvatarUrl;
    }

    public String getGroupTabName() {
        return groupTabName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupColor() {
        return groupColor;
    }

    public String getGroupMemberName() {
        return groupMemberName;
    }

    public int getGroupMemberCount() {
        return groupMemberCount;
    }


    public String getGroupTabId() {
        return groupTabId;
    }

    public void setGroupTabId(String groupTabId) {
        this.groupTabId = groupTabId;
    }
}
