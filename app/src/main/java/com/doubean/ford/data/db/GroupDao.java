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
import com.doubean.ford.data.vo.GroupPostsResult;
import com.doubean.ford.data.vo.GroupSearchResult;
import com.doubean.ford.data.vo.GroupTagPostsResult;
import com.doubean.ford.data.vo.Post;
import com.doubean.ford.data.vo.PostComment;
import com.doubean.ford.data.vo.PostCommentsResult;
import com.doubean.ford.data.vo.PostItem;
import com.doubean.ford.data.vo.PostTopComments;

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
    LiveData<GroupDetail> loadDetail(String groupId);

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    Long insertDetailIfNotExists(GroupDetail group);

    @Update(entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    void updateDetail(GroupDetail group);

    @Transaction
    default void upsertDetail(GroupDetail group) {
        if (insertDetailIfNotExists(group) == -1)
            updateDetail(group);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    Long insertBriefIfNotExists(GroupBrief group);

    @Update(entity = Group.class)
    @RewriteQueriesToDropUnusedColumns
    void updateDetail(GroupBrief group);

    @Transaction
    default void upsertDetail(GroupBrief group) {
        if (insertBriefIfNotExists(group) == -1)
            updateDetail(group);
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

    @Query("SELECT * FROM GroupSearchResult WHERE `query` = :query")
    GroupSearchResult findSearchResult(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupSearchResult(GroupSearchResult groupSearchResult);

    @Query("SELECT * FROM groups WHERE id IN (:groupIds)")
    @RewriteQueriesToDropUnusedColumns
    LiveData<List<GroupItem>> loadGroupsById(List<String> groupIds);

    default LiveData<List<GroupItem>> loadOrdered(List<String> groupIds, Comparator<GroupItem> c) {
        return Transformations.map(loadGroupsById(groupIds), groups -> {
            if (c == null) {
                groups.sort(Comparator.comparingInt(o -> groupIds.indexOf(o.id)));
            } else groups.sort(c);
            return groups;
        });
    }

    default LiveData<List<GroupItem>> loadOrdered(List<String> groupIds) {
        return loadOrdered(groupIds, null);
    }

    /* Posts-related operations start */

    @Query("SELECT * FROM Post WHERE groupId = :groupId ORDER BY created DESC")
    @RewriteQueriesToDropUnusedColumns
    LiveData<List<PostItem>> loadGroupPosts(String groupId);

    @Query("SELECT * FROM Post WHERE groupId = :groupId AND tagId=:tagId ORDER BY created DESC")
    @RewriteQueriesToDropUnusedColumns
    LiveData<List<PostItem>> loadGroupTagPosts(String groupId, String tagId);

    @Query("SELECT * FROM Post WHERE id IN (:postIds)")
    @RewriteQueriesToDropUnusedColumns
    LiveData<List<PostItem>> loadPostsById(List<String> postIds);

    default LiveData<List<PostItem>> loadPosts(List<String> postIds) {
        return Transformations.map(loadPostsById(postIds), posts -> {
            posts.sort(Comparator.comparingInt(o -> postIds.indexOf(o.id)));
            return posts;
        });
    }

    @Query("SELECT * FROM GroupPostsResult WHERE groupId = :groupId")
    @RewriteQueriesToDropUnusedColumns
    LiveData<GroupPostsResult> getGroupPosts(String groupId);


    @Query("SELECT * FROM GroupPostsResult WHERE groupId = :groupId")
    @RewriteQueriesToDropUnusedColumns
    GroupPostsResult findGroupPosts(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupPostsResult(GroupPostsResult groupPostsResult);


    @Query("SELECT * FROM GroupTagPostsResult WHERE groupId = :groupId AND tagId=:tagId")
    @RewriteQueriesToDropUnusedColumns
    LiveData<GroupTagPostsResult> getGroupTagPosts(String groupId, String tagId);

    @Query("SELECT * FROM GroupTagPostsResult WHERE groupId = :groupId AND tagId=:tagId")
    @RewriteQueriesToDropUnusedColumns
    GroupTagPostsResult findGroupTagPosts(String groupId, String tagId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupTagPostsResult(GroupTagPostsResult groupTagPostsResult);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPost(Post post);

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Post.class)
    @RewriteQueriesToDropUnusedColumns
    long insertPostItemIfNotExists(PostItem post);

    @Update(entity = Post.class)
    @RewriteQueriesToDropUnusedColumns
    void updatePostItem(PostItem post);

    @Transaction
    default void upsertPostItem(PostItem post) {
        if (insertPostItemIfNotExists(post) == -1)
            updatePostItem(post);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Post.class)
    @RewriteQueriesToDropUnusedColumns
    List<Long> insertPostsIfNotExist(List<PostItem> postList);

    @Update(entity = Post.class)
    @RewriteQueriesToDropUnusedColumns
    void updatePosts(List<PostItem> List);

    @Transaction
    default void upsertPosts(List<PostItem> postList) {
        List<PostItem> updateList = new ArrayList<>();
        List<Long> insertPosts = insertPostsIfNotExist(postList);
        for (int i = 0; i < insertPosts.size(); i++) {
            if (insertPosts.get(i) == -1)
                updateList.add(postList.get(i));
        }
        updatePosts(updateList);
    }

    @Query("SELECT * FROM Post WHERE id=:postId")
    LiveData<Post> loadPost(String postId);


    @Query("SELECT * FROM post_top_comments WHERE postId = :postId")
    LiveData<PostTopComments> loadPostTopComments(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPostTopComments(PostTopComments topComments);

    default LiveData<List<PostComment>> loadOrderedComments(List<String> commentIds, Comparator<PostComment> c) {
        return Transformations.map(loadCommentsById(commentIds), comments -> {
            if (c != null)
                comments.sort(c);
            else
                comments.sort(Comparator.comparingInt(o -> commentIds.indexOf(o.id)));
            return comments;
        });
    }

    default LiveData<List<PostComment>> loadOrderedComments(List<String> commentIds) {
        return loadOrderedComments(commentIds, null);
    }

    @Query("SELECT * FROM post_comments WHERE id IN (:commentIds)")
    LiveData<List<PostComment>> loadCommentsById(List<String> commentIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPostComments(List<PostComment> comments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPostCommentsResult(PostCommentsResult postCommentsResult);

    @Query("SELECT * FROM PostCommentsResult WHERE postId=:postId")
    LiveData<PostCommentsResult> getPostComments(String postId);

    @Query("SELECT * FROM PostCommentsResult WHERE postId=:postId")
    PostCommentsResult findPostComments(String postId);
}
