package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupFavorite;
import com.doubean.ford.data.GroupSearchResult;
import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.GroupTopicComment;
import com.doubean.ford.data.GroupTopicPopularComments;

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

    @Query("SELECT * FROM group_topic_popular_comments WHERE groupTopicId = :groupTopicId")
    LiveData<GroupTopicPopularComments> getGroupTopicPopularComments(String groupTopicId);

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

    @Query("SELECT * FROM group_topic_comments WHERE id IN (:commentIds)")
    LiveData<List<GroupTopicComment>> loadCommentsById(List<String> commentIds);

    default LiveData<List<GroupTopicComment>> loadOrderedComments(List<String> commentIds, Comparator<GroupTopicComment> c) {
        return Transformations.map(loadCommentsById(commentIds), comments -> {
            comments.sort(c);
            return comments;
        });
    }

    @Query("SELECT * FROM group_topic_comments WHERE topic_id = :topicId ORDER BY createTime ASC")
    LiveData<List<GroupTopicComment>> getTopicComments(String topicId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupTopicComments(List<GroupTopicComment> commentList);


    @Query("SELECT * FROM group_topics WHERE group_id = :groupId AND tag_id=:tagId ORDER BY date_created DESC LIMIT 100")
    LiveData<List<GroupTopic>> getGroupTopics(String groupId, String tagId);

    @Query("SELECT * FROM group_topics WHERE group_id = :groupId ORDER BY date_created DESC LIMIT 100")
    LiveData<List<GroupTopic>> getGroupTopics(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addGroupTopic(GroupTopic topic);

    @Query("SELECT * FROM group_topics WHERE id=:topicId")
    LiveData<GroupTopic> getGroupTopic(String topicId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupTopicPopularComments(GroupTopicPopularComments popularComments);


    //TODO
    //@Query("UPDATE groups " +
    //        "SET id=:group")
    //void conditionalInsertGroups(Group group);

}
