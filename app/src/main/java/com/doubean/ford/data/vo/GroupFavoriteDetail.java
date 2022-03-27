package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GroupFavoriteDetail {

    private final String groupMemberName;
    private final int groupMemberCount;
    private final String groupShortDesc;
    @NonNull
    private String groupId;
    private String groupName;
    private String groupAvatarUrl;
    private String groupTabName;
    private String groupColor;
    @Nullable
    private String groupTabId;

    public GroupFavoriteDetail(@NonNull Group group, @Nullable String groupTabId) {
        this.groupId = group.id;
        this.groupName = group.groupName;
        this.groupAvatarUrl = group.avatarUrl;
        this.groupTabId = groupTabId;
        if (groupTabId != null) {
            for (GroupPostTag tab : group.groupTabs) {
                if (tab.id.equals(groupTabId)) {
                    groupTabName = tab.name;
                }
            }
        }
        this.groupColor = group.colorString;
        this.groupMemberCount = group.memberCount;
        this.groupMemberName = group.memberName;
        this.groupShortDesc = group.getShortDesc();
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

    public String getGroupShortDesc() {
        return groupShortDesc;
    }

    public String getGroupTabId() {
        return groupTabId;
    }

    public void setGroupTabId(String groupTabId) {
        this.groupTabId = groupTabId;
    }
}
