package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Transaction;
import androidx.room.Update;

import com.doubean.ford.data.vo.Group;
import com.doubean.ford.data.vo.GroupBrief;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.GroupItem;
import com.doubean.ford.data.vo.GroupPost;
import com.doubean.ford.data.vo.GroupPostComment;
import com.doubean.ford.data.vo.GroupPostItem;
import com.doubean.ford.data.vo.GroupPostPopularComments;
import com.doubean.ford.data.vo.GroupSearchResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The Data Access Object for the [Group] class.
 * TODO: create base dao?
 */
@Dao
public interface GroupDao {
    @Query(value = "SELECT * FROM groups WHERE id = :groupId")
    @RewriteQueriesToDropUnusedColumns
    LiveData<GroupDetail> getGroup(String groupId);

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    Long insertGroupDetailIfNotExists(GroupDetail group);

    @Update(entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    void updateGroup(GroupDetail group);

    @Transaction
    default void upsertGroup(GroupDetail group) {
        if (insertGroupDetailIfNotExists(group) == -1)
            updateGroup(group);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    Long insertGroupBriefIfNotExists(GroupBrief group);

    @Update(entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    void updateGroup(GroupBrief group);

    @Transaction
    default void upsertGroup(GroupBrief group) {
        if (insertGroupBriefIfNotExists(group) == -1)
            updateGroup(group);
    }

    @Query("SELECT * FROM groups")
    @RewriteQueriesToDropUnusedColumns
    LiveData<List<GroupItem>> getGroups();

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    List<Long> insertGroupsIfNotExist(List<GroupItem> groupList);

    @Update(entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    void updateGroups(List<GroupItem> groupList);

    @Transaction
    default void upsertGroups(List<GroupItem> groupList) {
        List<GroupItem> updateList = new ArrayList<>();
        List<Long> insertGroups = insertGroupsIfNotExist(groupList);
        for (int i = 0; i < insertGroups.size(); i++) {
            if (insertGroups.get(i) == -1)
                updateList.add(groupList.get(i));
        }
        updateGroups(updateList);
    }


    @Query("SELECT * FROM GroupSearchResult WHERE `query` = :query")
    LiveData<GroupSearchResult> search(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupSearchResult(GroupSearchResult groupSearchResult);


    @Query("SELECT * FROM groups WHERE id IN (:groupIds)")
    @RewriteQueriesToDropUnusedColumns
    LiveData<List<GroupItem>> loadGroupsById(List<String> groupIds);

    default LiveData<List<GroupItem>> loadOrdered(List<String> groupIds, Comparator<GroupItem> c) {
        return Transformations.map(loadGroupsById(groupIds), groups -> {
            groups.sort(c);
            return groups;
        });
    }


    @Query("SELECT * FROM group_posts WHERE groupId = :groupId AND tagId=:tagId ORDER BY created DESC LIMIT 100")
    @RewriteQueriesToDropUnusedColumns
    LiveData<List<GroupPostItem>> getGroupPosts(String groupId, String tagId);

    @Query("SELECT * FROM group_posts WHERE groupId = :groupId ORDER BY created DESC LIMIT 100")
    @RewriteQueriesToDropUnusedColumns
    LiveData<List<GroupPostItem>> getGroupPosts(String groupId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPost(GroupPost post);

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = GroupPost.class)
    @RewriteQueriesToDropUnusedColumns
    long insertPostItemIfNotExists(GroupPostItem post);

    @Update(entity = GroupPost.class)
    @RewriteQueriesToDropUnusedColumns
    void updatePostItem(GroupPostItem post);

    @Transaction
    default void upsertPostItem(GroupPostItem group) {
        if (insertPostItemIfNotExists(group) == -1)
            updatePostItem(group);
    }

    @Query("SELECT * FROM group_posts WHERE id=:postId")
    LiveData<GroupPost> getGroupPost(String postId);

    @Query("SELECT * FROM group_post_comments WHERE id IN (:commentIds)")
    LiveData<List<GroupPostComment>> loadCommentsById(List<String> commentIds);

    default LiveData<List<GroupPostComment>> loadOrderedComments(List<String> commentIds, Comparator<GroupPostComment> c) {
        return Transformations.map(loadCommentsById(commentIds), comments -> {
            comments.sort(c);
            return comments;
        });
    }

    @Query("SELECT * FROM group_post_popular_comments WHERE groupPostId = :groupPostId")
    LiveData<GroupPostPopularComments> getGroupPostPopularComments(String groupPostId);

    @Query("SELECT * FROM group_post_comments WHERE post_id = :postId ORDER BY created ASC")
    LiveData<List<GroupPostComment>> getPostComments(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupPostComments(List<GroupPostComment> commentList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupPostPopularComments(GroupPostPopularComments popularComments);
}
