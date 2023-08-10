package com.doubean.ford.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.doubean.ford.api.DoubanService
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.notifications.Notifier
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
            DoubanService.create(), Notifier(applicationContext)
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
        const val WORK_NAME = "com.doubean.ford.work.RecommendPostsWorker"
    }
}