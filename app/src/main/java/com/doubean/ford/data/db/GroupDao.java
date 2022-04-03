package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.doubean.ford.data.vo.Group;
import com.doubean.ford.data.vo.GroupFavorite;
import com.doubean.ford.data.vo.GroupPost;
import com.doubean.ford.data.vo.GroupPostComment;
import com.doubean.ford.data.vo.GroupPostPopularComments;
import com.doubean.ford.data.vo.GroupSearchResult;

import java.util.Comparator;
import java.util.List;

/**
 * The Data Access Object for the [Group] class.
 */
@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups")
    LiveData<List<Group>> getGroups();

    @Query("SELECT * FROM groups WHERE id = :groupId")
    LiveData<Group> getGroup(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroup(Group... groups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroups(List<Group> groupList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteGroup(GroupFavorite groupFavorite);

    @Delete
    void deleteFavoriteGroup(GroupFavorite groupFavorite);

    @Query("SELECT * FROM GroupSearchResult WHERE `query` = :query")
    LiveData<GroupSearchResult> search(String query);

    @Query("SELECT * FROM group_post_popular_comments WHERE groupPostId = :groupPostId")
    LiveData<GroupPostPopularComments> getGroupPostPopularComments(String groupPostId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupSearchResult(GroupSearchResult groupSearchResult);

    @Query("SELECT * FROM groups WHERE id IN (:groupIds)")
    LiveData<List<Group>> loadGroupsById(List<String> groupIds);

    default LiveData<List<Group>> loadOrdered(List<String> groupIds, Comparator<Group> c) {

        return Transformations.map(loadGroupsById(groupIds), groups -> {
            groups.sort(c);
            return groups;
        });
    }

    @Query("SELECT * FROM group_post_comments WHERE id IN (:commentIds)")
    LiveData<List<GroupPostComment>> loadCommentsById(List<String> commentIds);

    default LiveData<List<GroupPostComment>> loadOrderedComments(List<String> commentIds, Comparator<GroupPostComment> c) {
        return Transformations.map(loadCommentsById(commentIds), comments -> {
            comments.sort(c);
            return comments;
        });
    }

    @Query("SELECT * FROM group_post_comments WHERE post_id = :postId ORDER BY created ASC")
    LiveData<List<GroupPostComment>> getPostComments(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupPostComments(List<GroupPostComment> commentList);


    @Query("SELECT * FROM group_posts WHERE group_id = :groupId AND tag_id=:tagId ORDER BY created DESC LIMIT 100")
    LiveData<List<GroupPost>> getGroupPosts(String groupId, String tagId);

    @Query("SELECT * FROM group_posts WHERE group_id = :groupId ORDER BY created DESC LIMIT 100")
    LiveData<List<GroupPost>> getGroupPosts(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addGroupPost(GroupPost post);

    @Query("SELECT * FROM group_posts WHERE id=:postId")
    LiveData<GroupPost> getGroupPost(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupPostPopularComments(GroupPostPopularComments popularComments);


    //TODO
    //@Query("UPDATE groups " +
    //        "SET id=:group")
    //void conditionalInsertGroups(Group group);

}
