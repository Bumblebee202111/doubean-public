package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupFavorite;

import java.util.List;

@Dao
public interface GroupFavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupFavorite(GroupFavorite groupFavorite);

    @Query("DELETE FROM group_favorites WHERE group_id=:groupId AND group_tab_id=:groupTabId")
    void deleteGroupFavorite(String groupId, String groupTabId);

    @Transaction
    @Query("SELECT * FROM groups WHERE id IN (SELECT id FROM group_favorites)")
    LiveData<List<Group>> getAllFavGroups();

    @Query("SELECT * FROM group_favorites")
    LiveData<List<GroupFavorite>> getFavoriteGroupAndTabIds();

    @Query("SELECT EXISTS(SELECT * FROM group_favorites WHERE group_id= :groupId and group_tab_id=:groupTabId)")
    LiveData<Boolean> getFavorite(String groupId, String groupTabId);

    @Query("SELECT * FROM group_favorites WHERE group_id= :groupId")
    LiveData<List<GroupFavorite>> getGroupFavorites(String groupId);
    //TODO
    //@Query("UPDATE groups " +
    //        "SET id=:group")
    //void conditionalInsertGroups(Group group);

}
