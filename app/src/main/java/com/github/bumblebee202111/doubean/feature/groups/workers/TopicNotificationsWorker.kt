package com.github.bumblebee202111.doubean.feature.groups.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.feature.groups.data.GroupRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@HiltWorker
class TopicNotificationsWorker @AssistedInject constructor(
    @Assisted appContext: Context, @Assisted params: WorkerParameters,
    private val groupRepository: GroupRepository,
    @Dispatcher(
        AppDispatchers.IO
    ) private val ioDispatcher: CoroutineDispatcher,
) :
    CoroutineWorker(appContext, params) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo()
    }

    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            val loadedSuccessfully = groupRepository.getNextTargetTopicNotifications()
            if (loadedSuccessfully)
                Result.success()
            else
                Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "com.github.bumblebee202111.doubean.work.RecommendTopicsWorker"
    }
}