package com.github.bumblebee202111.doubean

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.bumblebee202111.doubean.core.theme.DoubeanTheme
import com.github.bumblebee202111.doubean.data.auth.AuthRepository
import com.github.bumblebee202111.doubean.feature.groups.workers.TopicNotificationsWorker
import com.github.bumblebee202111.doubean.feature.groups.workers.TopicNotificationsWorker.Companion.WORK_NAME
import com.github.bumblebee202111.doubean.model.theme.ThemeMode
import com.github.bumblebee202111.doubean.navigation.TopLevelDestination
import com.github.bumblebee202111.doubean.navigation.toNavKeyOrNull
import com.github.bumblebee202111.doubean.ui.DoubeanApp
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var workManager: WorkManager

    @Inject
    lateinit var authRepository: AuthRepository

    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var snackbarManager: SnackbarManager

    private var pendingDeepLinkKey by mutableStateOf<NavKey?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        pendingDeepLinkKey = intent.data?.toString()?.toNavKeyOrNull()

        setContent {
            val startupTabName by viewModel.startupTab.collectAsStateWithLifecycle()
            val visibleTabNames by viewModel.visibleTabs.collectAsStateWithLifecycle()
            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val useDynamicColor by viewModel.useDynamicColor.collectAsStateWithLifecycle()

            if (startupTabName != null && visibleTabNames != null && themeMode != null && useDynamicColor != null) {
                val systemDarkTheme = isSystemInDarkTheme()
                val darkTheme = when (themeMode!!) {
                    ThemeMode.SYSTEM -> systemDarkTheme
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                }

                DisposableEffect(darkTheme) {
                    val barStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                    enableEdgeToEdge(
                        statusBarStyle = barStyle,
                        navigationBarStyle = barStyle,
                    )
                    onDispose {}
                }

                DoubeanTheme(
                    themeMode = themeMode!!,
                    dynamicColor = useDynamicColor!!,
                ) {
                    val topLevelDestinations =
                        TopLevelDestination.entries.filter { it.name in visibleTabNames!! }

                    val startRoute =
                        TopLevelDestination.entries.find { it.name == startupTabName }?.route
                            ?: topLevelDestinations.first().route

                    DoubeanApp(
                        snackbarManager = snackbarManager,
                        startRoute = startRoute as NavKey,
                        topLevelDestinations = topLevelDestinations,
                        currentUser = currentUser,
                        pendingDeepLinkKey = pendingDeepLinkKey,
                        onDeepLinkConsumed = { pendingDeepLinkKey = null }
                    )
                }
            }
        }

        setupWorkManager()

        lifecycleScope.launch {
            viewModel.autoImportSessionAtStartup.take(1).collect {
                if (it == true) {
                    syncDoubanSession()
                }
            }

        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val deepLinkKey = intent.data?.toString()?.toNavKeyOrNull()
        if (deepLinkKey != null) {
            pendingDeepLinkKey = deepLinkKey
        }
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
            viewModel.enableNotifications.collect { enableNotifications ->
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