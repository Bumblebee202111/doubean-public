package com.github.bumblebee202111.doubean.navigation

import androidx.navigation3.runtime.NavKey
import kotlin.reflect.KClass

class Navigator(val state: NavigationState) {
    fun navigate(key: NavKey) {
        if (key in state.backStacks.keys) {
            state.topLevelRoute = key
        } else {
            state.backStacks[state.topLevelRoute]?.add(key)
        }
    }

    fun navigate(backStack: List<NavKey>) {
        if (backStack.isEmpty()) return
        val topLevelTarget = backStack.firstOrNull { it in state.backStacks.keys }
        if (topLevelTarget != null) {
            state.topLevelRoute = topLevelTarget
            val targetStack = state.backStacks[topLevelTarget] ?: return
            val targetIndex = backStack.indexOf(topLevelTarget)
            val subKeys = backStack.subList(targetIndex + 1, backStack.size)
            for (key in subKeys) {
                if (targetStack.lastOrNull() != key) {
                    targetStack.add(key)
                }
            }
        } else {
            for (key in backStack) {
                navigate(key)
            }
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    fun popUpTo(routeClass: KClass<out NavKey>, inclusive: Boolean = false) {
        val currentStack = state.backStacks[state.topLevelRoute] ?: return

        val index = currentStack.indexOfLast { it::class == routeClass }
        if (index != -1) {
            val dropCount = currentStack.size - index - (if (inclusive) 0 else 1)
            repeat(dropCount) {
                currentStack.removeLastOrNull()
            }
        }
    }
}