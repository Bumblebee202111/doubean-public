package com.github.bumblebee202111.doubean.notifications.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.model.groups.TopicItemWithGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(private val appDatabase: AppDatabase) {

    private val userGroupDao = appDatabase.userGroupDao()


    fun getTopicNotifications(): Flow<PagingData<TopicItemWithGroup>> {
        val pagingConfig = PagingConfig(NOTIFICATION_PAGE_SIZE)
        return Pager(pagingConfig) {
            userGroupDao.topicNotificationsPagingSource()
        }.flow.map { pagingData ->
            pagingData.map(PopulatedTopicNotificationItem::asExternalModel)
        }
    }

    companion object {
        const val NOTIFICATION_PAGE_SIZE = 20
    }
}