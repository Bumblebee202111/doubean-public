package com.github.bumblebee202111.doubean

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.databinding.ActivityMainBinding
import com.github.bumblebee202111.doubean.workers.RecommendPostsWorker
import com.github.bumblebee202111.doubean.workers.RecommendPostsWorker.Companion.WORK_NAME
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var workManager: WorkManager

    @Inject
    lateinit var authRepository: AuthRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navController = findNavController(this, R.id.nav_host_fragment_activity_main)

        lifecycleScope.launch {
            mainActivityViewModel.startAppWithGroups.collect {
                if (it == null) return@collect
                val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
                navGraph.setStartDestination(
                    when (it) {
                        true -> R.id.navigation_groups_graph
                        false -> R.id.nav_home
                    }
                )
                navController.graph = navGraph
                setupWithNavController(navView, navController)
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupWorkManager()

        syncDoubanSession()

    }

    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private fun syncDoubanSession() {
        authRepository.syncSessionFromDoubanPrefs()
    }

    private fun setupWorkManager() {
        workManager = WorkManager.getInstance(this)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val notifyPostRecommendationsRequest =
            PeriodicWorkRequestBuilder<RecommendPostsWorker>(
                15,
                TimeUnit.MINUTES
            ).setConstraints(constraints).build()

        lifecycleScope.launch {
            mainActivityViewModel.enableNotifications.collect { enableNotifications ->
                when (enableNotifications) {
                    true -> {
                        workManager.enqueueUniquePeriodicWork(
                            WORK_NAME,
                            ExistingPeriodicWorkPolicy.KEEP, notifyPostRecommendationsRequest
                        )
                    }

                    false -> {
                        workManager.cancelUniqueWork(WORK_NAME)
                    }

                    null -> {

                    }
                }
            }
        }

    }


}