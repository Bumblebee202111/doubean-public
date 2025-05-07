@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.groups.topic

import android.util.Log
import androidx.core.text.htmlEncode
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.GroupTopicRepository
import com.github.bumblebee202111.doubean.data.repository.PollRepository
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.TopicRoute
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.CachedAppResult
import com.github.bumblebee202111.doubean.model.data
import com.github.bumblebee202111.doubean.model.fangorns.ReactionType
import com.github.bumblebee202111.doubean.model.groups.Poll
import com.github.bumblebee202111.doubean.model.groups.PollId
import com.github.bumblebee202111.doubean.model.groups.Question
import com.github.bumblebee202111.doubean.model.groups.QuestionId
import com.github.bumblebee202111.doubean.model.groups.TopicCommentSortBy
import com.github.bumblebee202111.doubean.model.groups.TopicContentEntityId
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val pollRepository: PollRepository,
    val authRepository: AuthRepository,
    private val topicRepository: GroupTopicRepository,
    savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {

    val topicId = savedStateHandle.toRoute<TopicRoute>().topicId

    private val commentsDataFlows = topicRepository.getTopicCommentsData(topicId)

    val popularComments = commentsDataFlows.first

    val allComments = commentsDataFlows.second.cachedIn(viewModelScope)

    private val _commentsSortBy: MutableStateFlow<TopicCommentSortBy> =
        MutableStateFlow(TopicCommentSortBy.ALL)
    val commentsSortBy = _commentsSortBy.asStateFlow()

    val shouldShowSpinner = commentsDataFlows.first.map { it.isNotEmpty() }.stateInUi(false)

    private val topicResult = topicRepository.getTopic(topicId).stateInUi()

    val topic = topicResult.onEach { result ->
        if (result is CachedAppResult.Error) {
            snackbarManager.showSnackBar(result.error.asUiMessage())
        }
    }.map {
        it?.data
    }.stateInUi()

    val contentHtml = topicResult.flatMapLatest { topicResultData ->
        val content = topicResultData?.data?.content
        if (topicResultData == null || topicResultData is CachedAppResult.Loading || content == null) {
            return@flatMapLatest flowOf(null)
        }

        val matchResults = CONTENT_ENTITY_REGEX.findAll(content)

        val ids: List<TopicContentEntityId> = matchResults.mapNotNull { matchResult ->
            val groupValues = matchResult.groupValues
            when (groupValues[2]) {
                "poll" -> {
                    PollId(groupValues[3])
                }

                "question" -> {
                    QuestionId(groupValues[3])
                }

                else -> {
                    Log.w(
                        "TopicViewModel",
                        "Unknown entity type found in content: ${groupValues.getOrNull(2)}"
                    )
                    null
                }
            }
        }.toList()

        if (ids.isEmpty()) {
            return@flatMapLatest flowOf(formatHtmlContent(content))
        }

        flowOf(pollRepository.getPollsAndQuestions(ids)).map { entitiesResult ->
            when (entitiesResult) {
                is AppResult.Error -> {
                    snackbarManager.showSnackBar(entitiesResult.error.asUiMessage())
                    formatHtmlContent(content)
                }

                is AppResult.Success -> {
                    val entities = entitiesResult.data
                    val expandedContentElement =
                        content.replace(CONTENT_ENTITY_REGEX) { matchResult ->
                            val groupValues = matchResult.groupValues

                            buildString {
                                append(groupValues[1]) 
                                entities.forEach {
                                    when (it) {
                                        is Poll -> append(buildPollHtml(it))
                                        is Question -> append(buildQuestionHtml(it))
                                    }
                                }
                                append(groupValues[6]) 
                            }
                        }
                    Log.d("Topic detail", "content: $expandedContentElement")
                    formatHtmlContent(expandedContentElement)
                }
            }
        }
    }.stateInUi()

    val isLoggedIn = authRepository.isLoggedIn().stateInUi(false)

    fun react(isVote: Boolean) {
        viewModelScope.launch {
            val result = topicRepository.react(
                topicId = topicId,
                reactionType = if (isVote) ReactionType.TYPE_VOTE else ReactionType.TYPE_CANCEL_VOTE
            )
            when (result) {
                is AppResult.Error -> {
                    snackbarManager.showSnackBar(result.error.asUiMessage())
                }

                is AppResult.Success -> Unit
            }
        }
    }

    val shouldShowPhotoList =
        topicResult.map { it?.data?.content?.contains("image-container") == false }
            .stateInUi()

    fun updateCommentsSortBy(commentSortBy: TopicCommentSortBy) {
        _commentsSortBy.value = commentSortBy
    }

    private fun roundedPercentage(f: Float): Double {
        return f.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toDouble()
    }

    fun displayInvalidImageUrl() {
        snackbarManager.showSnackBar("Invalid image Url".toUiMessage())
    }

    private fun buildPollHtml(entity: Poll): String {
        val showOptionCounts = entity.options.any { it.votedUserCount > 0 }
        val totalVotes = entity.votedUserCount.toFloat().coerceAtLeast(1f)

        val optionElements = entity.options.joinToString("") { option ->
            val percentage =
                if (showOptionCounts) roundedPercentage(option.votedUserCount / totalVotes * 100) else 0.0
            """
<label class="poll-label">
    <span class="poll-checkbox"><input type="radio" disabled></span>
    <span class="poll-option">
        <span class="poll-option-title">${option.title.htmlEncode()}</span>
        <span class="poll-option-voted-count">${if (showOptionCounts) " ${option.votedUserCount} (${percentage}%)" else ""}</span>
    </span>
</label>
""".trimIndent()
        }
        val pollType = if (entity.votedLimit == 1) "单选" else "最多选${entity.votedLimit}项"
        val expireTimeHtml =
            entity.expireTime?.let { "<span class=\"poll-expire-time\"> · Poll expires at $it</span>" }
                ?: ""

        return """
<div class="poll-wrapper rendered">
    <div class="poll-title">
        <span>${entity.title.htmlEncode()} ($pollType)</span>$expireTimeHtml
    </div>
    <div class="poll-meta">
        <span>${entity.votedUserCount}人参与</span>
    </div>
    <div class="poll-content">
        <form class="poll-options">$optionElements</form>
    </div>
</div>
""".trimIndent()
    }

    private fun buildQuestionHtml(entity: Question): String {
        val correctAnswerHtml = entity.correctAnswer.takeIf { it.isNotBlank() }?.let {
            """
<div class="question-content">
    <form>
        <div class="question-result">
            <div class="question-result-answer">正确答案：${it.htmlEncode()}</div>
        </div>
    </form>
</div>
""".trimIndent()
        } ?: ""
        val expireTimeHtml =
            entity.expireTime?.let { "<span class=\"question-expire-time\"> · Question expires at $it</span>" }
                ?: ""
        val renderedClass =
            "question-wrapper rendered${if (entity.correctAnswer.isNotBlank()) " has-correct" else ""}"

        return """
<div class="$renderedClass">
    <div class="question-title">
        <span>${entity.title.htmlEncode()}</span>$expireTimeHtml
    </div>
    <div class="question-meta"><span>${entity.postedUserCount}人参与</span></div>
    $correctAnswerHtml
</div>
""".trimIndent()
    }

    private fun formatHtmlContent(content: String): String {
        return """
        <!DOCTYPE html>
        <head>
            <meta name="viewport" content="width=device-width, height=device-height, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, viewport-fit=cover" />
        </head>
        <body>
            $content
        <body>
        """.trimIndent()
    }

    companion object {
        private val CONTENT_ENTITY_REGEX =
            "(<div data-entity-type=\"(poll|question)\" data-id=\"(\\d*)\">)<div class=\"(poll|question)-wrapper\"><div class=\"(poll|question)-title\"><span>[^<>]*</span></div></div>(</div>)".toRegex()
    }
}