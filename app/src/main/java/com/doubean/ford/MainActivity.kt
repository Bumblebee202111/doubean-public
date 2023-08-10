package com.doubean.ford

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.work.*
import com.doubean.ford.databinding.ActivityMainBinding
import com.doubean.ford.util.InjectorUtils
import com.doubean.ford.workers.RecommendPostsWorker
import com.doubean.ford.workers.RecommendPostsWorker.Companion.WORK_NAME
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        InjectorUtils.provideMainActivityViewModelFactory(
            this
        )
    }
    private lateinit var navController: NavController
    private lateinit var workManager: WorkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        setupWithNavController(binding.navView, navController)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupWorkManager()
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
        mainActivityViewModel.enableNotifications.observe(this) { enableNotifications ->
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
            }
        }
    }
}