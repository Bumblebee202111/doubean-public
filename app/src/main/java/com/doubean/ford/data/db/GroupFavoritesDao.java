package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.doubean.ford.data.GroupFavorite;

import java.util.List;

@Dao
public interface GroupFavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupFavorite(GroupFavorite groupFavorite);

    @Query("DELETE FROM group_favorites WHERE group_id=:groupId AND (group_tab_id=:groupTabId OR ( group_tab_id IS NULL AND :groupTabId IS NULL ))")
    void deleteGroupFavorite(String groupId, String groupTabId);
    @Query("SELECT * FROM group_favorites")
    LiveData<List<GroupFavorite>> getFavoriteGroupAndTabIds();

    @Query("SELECT EXISTS(SELECT * FROM group_favorites WHERE group_id= :groupId and  (group_tab_id=:groupTabId OR ( group_tab_id IS NULL AND :groupTabId IS NULL )))")
    LiveData<Boolean> getFavorite(String groupId, String groupTabId);

    //TODO
    //@Query("UPDATE groups " +
    //        "SET id=:group")
    //void conditionalInsertGroups(Group group);

}
