package com.github.bumblebee202111.doubean.feature.common

import com.github.bumblebee202111.doubean.data.repository.ItemDouListRepository
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.doulists.DouListItem
import com.github.bumblebee202111.doubean.model.doulists.ItemDouList
import com.github.bumblebee202111.doubean.model.structure.CollectionItem
import com.github.bumblebee202111.doubean.model.common.CollectType
import com.github.bumblebee202111.doubean.ui.common.CollectDialogUiState
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CollectionHandler(
    private val scope: CoroutineScope,
    private val itemDouListRepository: ItemDouListRepository,
    private val snackbarManager: SnackbarManager,
) {
    private val _collectDialogUiState = MutableStateFlow<CollectDialogUiState?>(null)
    val collectDialogUiState = _collectDialogUiState.asStateFlow()

    fun onCollectClick(type: CollectType, id: String) {
        scope.launch {
            _collectDialogUiState.value = CollectDialogUiState(isLoading = true)
            when (val result = itemDouListRepository.getItemAvailableDouLists(type, id)) {
                is AppResult.Success -> {
                    _collectDialogUiState.value =
                        CollectDialogUiState(douLists = result.data.douLists)
                }

                is AppResult.Error -> {
                    snackbarManager.showMessage(result.error.asUiMessage())
                    dismissCollectDialog()
                }
            }
        }
    }


    suspend fun toggleCollectionInDouList(
        type: CollectType,
        id: String,
        douList: ItemDouList,
    ): Boolean? {
        val isCollecting = !douList.isCollected
        var newIsCollected: Boolean? = null

        val result = if (isCollecting) {
            itemDouListRepository.collectItem(type, id, douList.id)
        } else {
            itemDouListRepository.uncollectItem(type, id, douList.id)
        }

        when (result) {
            is AppResult.Success -> {
                newIsCollected = when (val data = result.data) {
                    is DouListItem -> true
                    is CollectionItem -> data.isCollected
                    else -> null
                }

                updateDialogUiState(douList.id, isCollecting)
                delay(300L)
                dismissCollectDialog()
            }

            is AppResult.Error -> {
                snackbarManager.showMessage(result.error.asUiMessage())
            }
        }
        return newIsCollected
    }

    fun dismissCollectDialog() {
        _collectDialogUiState.value = null
    }

    private fun updateDialogUiState(douListId: String, newIsCollectedForList: Boolean) {
        _collectDialogUiState.update { currentUiState ->
            currentUiState?.copy(
                douLists = currentUiState.douLists.map {
                    if (it.id == douListId) it.copy(isCollected = newIsCollectedForList) else it
                }
            )
        }
    }
}