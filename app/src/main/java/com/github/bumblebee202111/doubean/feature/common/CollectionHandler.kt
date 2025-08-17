package com.github.bumblebee202111.doubean.feature.common

import com.github.bumblebee202111.doubean.data.repository.DouListRepository
import com.github.bumblebee202111.doubean.data.repository.ItemDouListRepository
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.common.CollectType
import com.github.bumblebee202111.doubean.model.doulists.DouListItem
import com.github.bumblebee202111.doubean.model.doulists.ItemDouList
import com.github.bumblebee202111.doubean.model.structure.CollectionItem
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
    private val douListRepository: DouListRepository,
    private val snackbarManager: SnackbarManager,
) {
    private val _collectDialogUiState = MutableStateFlow<CollectDialogUiState?>(null)
    val collectDialogUiState = _collectDialogUiState.asStateFlow()

    private val _showCreateDialogEvent = MutableStateFlow(false)
    val showCreateDialogEvent = _showCreateDialogEvent.asStateFlow()

    fun showCollectDialog(type: CollectType, id: String) {
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

    
    suspend fun toggleCollection(
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

    fun showCreateDialog() {
        _showCreateDialogEvent.value = true
    }

    fun dismissCreateDialog() {
        _showCreateDialogEvent.value = false
    }

    suspend fun createAndCollect(
        title: String,
        type: CollectType,
        id: String,
    ): Boolean {
        val createResult = douListRepository.createDouList(title)
        if (createResult is AppResult.Error) {
            snackbarManager.showMessage(createResult.error.asUiMessage())
            return false
        }

        val newDouList = (createResult as AppResult.Success).data
        val collectResult = itemDouListRepository.collectItem(type, id, newDouList.id)
        if (collectResult is AppResult.Error) {
            snackbarManager.showMessage(collectResult.error.asUiMessage())
            return false
        }

        dismissCreateDialog()

        val newItemDouList = ItemDouList(
            id = newDouList.id,
            title = newDouList.title,
            uri = newDouList.uri,
            alt = newDouList.alt,
            type = newDouList.type,
            sharingUrl = newDouList.sharingUrl,
            coverUrl = newDouList.coverUrl,
            isFollowed = true,
            createTime = newDouList.createTime,
            owner = newDouList.owner,
            category = newDouList.category,
            isMergedCover = newDouList.isMergedCover,
            followersCount = newDouList.followersCount,
            isPrivate = newDouList.isPrivate,
            updateTime = newDouList.updateTime,
            doulistType = newDouList.doulistType,
            doneCount = newDouList.doneCount,
            itemCount = newDouList.itemCount,
            isSysPrivate = newDouList.isSysPrivate,
            listType = newDouList.listType,
            isCollected = true,
        )

        _collectDialogUiState.update { currentState ->
            currentState?.copy(
                douLists = listOf(newItemDouList) + (currentState.douLists)
            )
        }

        delay(300L)
        dismissCollectDialog()

        return true
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