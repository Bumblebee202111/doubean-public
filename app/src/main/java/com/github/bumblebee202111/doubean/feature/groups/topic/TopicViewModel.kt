@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.groups.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.auth.AuthRepository
import com.github.bumblebee202111.doubean.data.doulist.DouListRepository
import com.github.bumblebee202111.doubean.data.group.GroupTopicRepository
import com.github.bumblebee202111.doubean.data.itemdoulist.ItemDouListRepository
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.model.common.CollectType
import com.github.bumblebee202111.doubean.model.data
import com.github.bumblebee202111.doubean.model.doulist.ItemDouList
import com.github.bumblebee202111.doubean.model.group.ReactionType
import com.github.bumblebee202111.doubean.model.group.TopicCommentSortBy
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.common.collection.CollectionHandler
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TopicViewModel.Factory::class)
class TopicViewModel @AssistedInject constructor(
    val authRepository: AuthRepository,
    private val topicRepository: GroupTopicRepository,
    itemDouListRepository: ItemDouListRepository,
    douListRepository: DouListRepository,
    private val snackbarManager: SnackbarManager,
    @Assisted("topicId") val topicId: String,
    @Assisted("spmId") val spmId: String?,
) : ViewModel() {

    private val _authorOnlyMode = MutableStateFlow(false)
    val authorOnlyMode = _authorOnlyMode.asStateFlow()

    private val commentsDataResult = authorOnlyMode.map { onlyOp ->
        topicRepository.getTopicCommentsData(topicId, spmId, onlyOp)
    }

    val popularComments = commentsDataResult
        .flatMapLatest { it.popularComments }
        .stateInUi(emptyList())

    val allComments = commentsDataResult
        .flatMapLatest { it.allCommentsPagingData }
        .cachedIn(viewModelScope)

    private val _commentsSortBy: MutableStateFlow<TopicCommentSortBy> =
        MutableStateFlow(TopicCommentSortBy.ALL)
    val commentsSortBy = _commentsSortBy.asStateFlow()

    val shouldShowSpinner = popularComments.map { it.isNotEmpty() }.stateInUi(false)

    private val retryTrigger = MutableStateFlow(0)

    val topicResult = retryTrigger.flatMapLatest {
        topicRepository.getTopic(topicId, spmId)
    }.stateInUi()

    val topic = topicResult.onEach { result ->
        if (result is CachedAppResult.Error) {
            snackbarManager.showMessage(result.error.asUiMessage())
        }
    }.map {
        it?.data
    }.stateInUi()

    val isLoggedIn = authRepository.isLoggedIn().stateInUi(false)

    private val collectionHandler = CollectionHandler(
        scope = viewModelScope,
        itemDouListRepository = itemDouListRepository,
        douListRepository = douListRepository,
        snackbarManager = snackbarManager
    )
    val collectDialogUiState = collectionHandler.collectDialogUiState
    val showCreateDouListDialog = collectionHandler.showCreateDialogEvent

    fun collect() {
        collectionHandler.showCollectDialog(CollectType.GROUP_TOPIC, topicId)
    }

    fun dismissCollectDialog() = collectionHandler.dismissCollectDialog()

    fun showCreateDialog() = collectionHandler.showCreateDialog()

    fun dismissCreateDialog() = collectionHandler.dismissCreateDialog()

    fun createAndCollect(title: String) {
        viewModelScope.launch {
            collectionHandler.createAndCollect(
                title = title,
                type = CollectType.GROUP_TOPIC,
                id = topicId
            )
        }
    }

    fun toggleCollection(douList: ItemDouList) {
        viewModelScope.launch {
            val newIsCollected = collectionHandler.toggleCollection(
                type = CollectType.GROUP_TOPIC,
                id = topicId,
                douList = douList
            )
            newIsCollected?.let {
                topicRepository.updateTopicIsCollected(topicId, it)
            }
        }
    }

    fun react(isVote: Boolean) {
        viewModelScope.launch {
            val result = topicRepository.react(
                topicId = topicId,
                reactionType = if (isVote) ReactionType.TYPE_VOTE else ReactionType.TYPE_CANCEL_VOTE
            )
            if (result is AppResult.Error) {
                snackbarManager.showMessage(result.error.asUiMessage())
            }
        }
    }

    val shouldShowPhotoList =
        topic.map { it?.content?.contains("image-container") == false }
            .stateInUi()

    fun updateCommentsSortBy(commentSortBy: TopicCommentSortBy) {
        _commentsSortBy.value = commentSortBy
    }

    fun updateAuthorOnlyMode(enabled: Boolean) {
        _authorOnlyMode.value = enabled
    }

    fun displayInvalidImageUrl() {
        snackbarManager.showMessage("Invalid image Url".toUiMessage())
    }

    fun refresh() {
        retryTrigger.value++
    }

    override fun onCleared() {
        super.onCleared()
        TopicWebViewPool.release(topicId)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("topicId") topicId: String,
            @Assisted("spmId") spmId: String?,
        ): TopicViewModel
    }
}
