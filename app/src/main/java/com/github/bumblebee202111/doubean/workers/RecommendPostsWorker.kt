package com.github.bumblebee202111.doubean.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.github.bumblebee202111.doubean.coroutines.AppDispatchers
import com.github.bumblebee202111.doubean.coroutines.Dispatcher
import com.github.bumblebee202111.doubean.data.repository.UserGroupRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext




@HiltWorker
class RecommendPostsWorker @AssistedInject constructor(
    @Assisted appContext: Context, @Assisted params: WorkerParameters,
    private val userGroupRepository: UserGroupRepository,
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
            val loadedSuccessfully = userGroupRepository.getRecommendedPosts()
            if (loadedSuccessfully)
                Result.success()
            else
                Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "com.github.bumblebee202111.doubean.work.RecommendPostsWorker"
    }
}