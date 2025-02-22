package com.github.bumblebee202111.doubean

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.ui.DoubeanApp
import com.github.bumblebee202111.doubean.ui.theme.AppTheme
import com.github.bumblebee202111.doubean.workers.TopicNotificationsWorker
import com.github.bumblebee202111.doubean.workers.TopicNotificationsWorker.Companion.WORK_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager

    @Inject
    lateinit var authRepository: AuthRepository

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                val startAppWithGroups by mainActivityViewModel.startAppWithGroups.collectAsStateWithLifecycle()
                startAppWithGroups?.let {
                    DoubeanApp(
                        navController = rememberNavController(),
                        startWithGroups = it
                    )
                }
            }
        }

        setupWorkManager()

        syncDoubanSession()
    }

    private fun syncDoubanSession() {
        authRepository.syncSessionFromDoubanPrefs()
    }

    private fun setupWorkManager() {
        workManager = WorkManager.getInstance(this)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val notifyTopicsRequest =
            PeriodicWorkRequestBuilder<TopicNotificationsWorker>(
                15,
                TimeUnit.MINUTES
            ).setConstraints(constraints).build()

        lifecycleScope.launch {
            mainActivityViewModel.enableNotifications.collect { enableNotifications ->
                when (enableNotifications) {
                    true -> {
                        workManager.enqueueUniquePeriodicWork(
                            WORK_NAME,
                            ExistingPeriodicWorkPolicy.KEEP, notifyTopicsRequest
                        )
                    }

                    false -> {
                        workManager.cancelUniqueWork(WORK_NAME)
                    }

                    null -> Unit
                }
            }
        }
    }

}