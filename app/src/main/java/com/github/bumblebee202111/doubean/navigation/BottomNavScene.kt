package com.github.bumblebee202111.doubean.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope


class BottomNavScene(
    val entry: NavEntry<NavKey>,
    override val previousEntries: List<NavEntry<NavKey>>,
    val navigator: Navigator,
) : Scene<NavKey> {

    override val key: Any = "BottomNavSceneKey"

    override val entries: List<NavEntry<NavKey>> = listOf(entry)

    override val content: @Composable () -> Unit = {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                entry.Content()
            }

            NavigationBar {
                TopLevelDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.iconVector, contentDescription = null) },
                        label = { Text(stringResource(destination.labelResId)) },
                        selected = entry.contentKey == destination.route.toString(),
                        onClick = { navigator.navigate(destination.route as NavKey) }
                    )
                }
            }
        }
    }
}


class BottomNavSceneStrategy(
    topLevelRoutes: Set<NavKey>,
    private val navigator: Navigator,
) : SceneStrategy<NavKey> {

    private val topLevelContentKeys = topLevelRoutes.map { it.toString() }.toSet()

    override fun SceneStrategyScope<NavKey>.calculateScene(entries: List<NavEntry<NavKey>>): Scene<NavKey>? {
        val currentEntry = entries.lastOrNull() ?: return null

        
        val isTopLevel = currentEntry.contentKey in topLevelContentKeys

        if (isTopLevel) {
            return BottomNavScene(
                entry = currentEntry,
                previousEntries = entries.dropLast(1),
                navigator = navigator
            )
        }

        return null
    }
}