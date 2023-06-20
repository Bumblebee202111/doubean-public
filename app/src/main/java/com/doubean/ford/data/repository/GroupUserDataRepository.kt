package com.doubean.ford.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.db.UserGroupDao
import com.doubean.ford.data.db.model.FollowedGroupEntity
import com.doubean.ford.data.db.model.FollowedGroupTabEntity
import com.doubean.ford.data.db.model.PopulatedGroupFollowItem
import com.doubean.ford.data.db.model.asExternalModel
import com.doubean.ford.model.GroupFollowItem

class GroupUserDataRepository private constructor(
    appDatabase: AppDatabase,
) {
    private val userGroupDao: UserGroupDao = appDatabase.groupFollowsAndSavesDao()

    fun getGroupFollowed(groupId: String): LiveData<Boolean> {
        return userGroupDao.loadGroupFollowed(groupId)
    }

    fun getTabFollowed(tabId: String): LiveData<Boolean> {
        return userGroupDao.loadTabFollowed(tabId)
    }


    suspend fun removeFollowedGroup(groupId: String) {
        userGroupDao.deleteFollowedGroup(groupId)
    }

    suspend fun addFollowedGroup(groupId: String) {
        userGroupDao.insertFollowedGroup(FollowedGroupEntity(groupId))
    }

    suspend fun removeFollowedTab(tabId: String) {
        userGroupDao.deleteFollowedTab(tabId)
    }

    suspend fun addFollowedTab(tabId: String) {
        userGroupDao.insertFollowedTab(FollowedGroupTabEntity(tabId))
    }

    fun getAllGroupFollows(): LiveData<List<GroupFollowItem>> =
        userGroupDao.loadAllFollows().map {
            it.map(PopulatedGroupFollowItem::asExternalModel)
        }

    companion object {
        @Volatile
        private var instance: GroupUserDataRepository? = null
        fun getInstance(
            appDatabase: AppDatabase,
        ): GroupUserDataRepository? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GroupUserDataRepository(
                            appDatabase
                        )
                    }
                }
            }
            return instance
        }
    }
}