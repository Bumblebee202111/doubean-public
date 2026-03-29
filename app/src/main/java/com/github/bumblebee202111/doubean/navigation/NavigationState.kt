package com.github.bumblebee202111.doubean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
): NavigationState {

    val topLevelRouteState = rememberSaveable(
        saver = Saver(
            save = { it.value.toString() },
            restore = { savedString ->
                val restoredKey = topLevelRoutes.find { it.toString() == savedString } ?: startRoute
                mutableStateOf(restoredKey)
            }
        )
    ) {
        mutableStateOf(startRoute)
    }

    val currentBackStacks = topLevelRoutes.associateWith { routeKey ->
        key(routeKey.toString()) {
            rememberNavBackStack(routeKey)
        }
    }

    
    val navigationState = remember {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRouteState,
            backStacks = currentBackStacks
        )
    }

    
    
    SideEffect {
        navigationState.startRoute = startRoute
        navigationState.backStacks = currentBackStacks

        
        if (navigationState.topLevelRoute !in topLevelRoutes) {
            navigationState.topLevelRoute = startRoute
        }
    }

    return navigationState
}

class NavigationState(
    var startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    var backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var topLevelRoute: NavKey by topLevelRoute

    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}