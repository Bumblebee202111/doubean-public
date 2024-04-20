package com.github.bumblebee202111.doubean.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.repository.GroupUserDataRepository
import com.github.bumblebee202111.doubean.network.ApiRetrofitService
import com.github.bumblebee202111.doubean.notifications.Notifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// There might be a post module in the future
// Currently, "recommend posts", "feed", "notify" are interchangeable in many places

class RecommendPostsWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo()
    }

    override suspend fun doWork(): Result {
        val repository = GroupUserDataRepository.getInstance(
            AppDatabase.getInstance(applicationContext)!!,
            ApiRetrofitService.create(), Notifier(applicationContext)
        )!!
        return withContext(Dispatchers.IO) {
            val loadedSuccessfully = repository.getRecommendedPosts()
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