package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;


public class RecommendedGroup {

    @Embedded
    public RecommendedGroupResult recommendedGroupResult;

    @Relation(parentColumn = "groupId", entityColumn = "id", entity = Group.class)
    @NonNull
    public GroupItem group;

    @Relation(parentColumn = "postId", entityColumn = "id", entity = Post.class)
    @Nullable
    public PostItem post;

}
