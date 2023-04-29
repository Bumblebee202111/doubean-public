package com.doubean.ford.data.repository

import androidx.lifecycle.LiveData
import com.doubean.ford.api.DoubanService
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.db.GroupFollowsAndSavesDao
import com.doubean.ford.data.vo.GroupFollow
import com.doubean.ford.util.AppExecutors

class GroupsFollowsAndSavesRepository private constructor(
    private val appExecutors: AppExecutors,
    appDatabase: AppDatabase,
    doubanService: DoubanService
) {
    private val groupFollowsAndSavesDao: GroupFollowsAndSavesDao
    private val doubanService: DoubanService
    private val appDatabase: AppDatabase

    init {
        groupFollowsAndSavesDao = appDatabase.groupFollowsAndSavesDao()
        this.doubanService = doubanService
        this.appDatabase = appDatabase
    }

    val followIds: LiveData<List<GroupFollow>>
        get() = groupFollowsAndSavesDao.loadFollowIds()

    fun getFollowed(groupId: String, tabId: String?): LiveData<Boolean> {
        return groupFollowsAndSavesDao.loadFollowed(groupId, tabId)
    }

    fun removeFollow(groupId: String, tabId: String?) {
        appExecutors.diskIO().execute { groupFollowsAndSavesDao.deleteFollow(groupId, tabId!!) }
    }

    fun createFollow(groupId: String?, groupTabId: String?) {
        val groupFollow = GroupFollow(groupId!!, groupTabId, 0)
        appExecutors.diskIO().execute { groupFollowsAndSavesDao.insertFollow(groupFollow) }
    }

    companion object {
        private var instance: GroupsFollowsAndSavesRepository? = null
        @JvmStatic
        fun getInstance(
            appExecutors: AppExecutors,
            appDatabase: AppDatabase,
            doubanService: DoubanService
        ): GroupsFollowsAndSavesRepository? {
            if (instance == null) {
                synchronized(GroupsFollowsAndSavesRepository::class.java) {
                    if (instance == null) {
                        instance = GroupsFollowsAndSavesRepository(
                            appExecutors,
                            appDatabase,
                            doubanService
                        )
                    }
                }
            }
            return instance
        }
    }
}