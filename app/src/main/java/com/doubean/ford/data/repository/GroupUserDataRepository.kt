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
import com.doubean.ford.util.AppExecutors

class GroupUserDataRepository private constructor(
    private val appExecutors: AppExecutors,
    appDatabase: AppDatabase,
) {
    private val userGroupDao: UserGroupDao = appDatabase.groupFollowsAndSavesDao()

    fun getGroupFollowed(groupId: String): LiveData<Boolean> {
        return userGroupDao.loadGroupFollowed(groupId)
    }

    fun getTabFollowed(tabId: String): LiveData<Boolean> {
        return userGroupDao.loadTabFollowed(tabId)
    }


    fun removeFollowedGroup(groupId: String) {
        appExecutors.diskIO().execute { userGroupDao.deleteFollowedGroup(groupId) }
    }

    fun addFollowedGroup(groupId: String) {
        appExecutors.diskIO()
            .execute { userGroupDao.insertFollowedGroup(FollowedGroupEntity(groupId)) }
    }

    fun removeFollowedTab(tabId: String) {
        appExecutors.diskIO().execute { userGroupDao.deleteFollowedTab(tabId) }
    }

    fun addFollowedTab(tabId: String) {
        appExecutors.diskIO()
            .execute { userGroupDao.insertFollowedTab(FollowedGroupTabEntity(tabId)) }
    }

    fun getAllGroupFollows(): LiveData<List<GroupFollowItem>> =
        userGroupDao.loadAllFollows().map {
            it.map(PopulatedGroupFollowItem::asExternalModel)
        }

    companion object {
        @Volatile
        private var instance: GroupUserDataRepository? = null
        fun getInstance(
            appExecutors: AppExecutors,
            appDatabase: AppDatabase,
        ): GroupUserDataRepository? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GroupUserDataRepository(
                            appExecutors,
                            appDatabase
                        )
                    }
                }
            }
            return instance
        }
    }
}