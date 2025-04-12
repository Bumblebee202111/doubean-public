package com.github.bumblebee202111.doubean.feature.groups.topic

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.htmlEncode
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.GroupTopicRepo
import com.github.bumblebee202111.doubean.data.repository.PollRepository
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.TopicRoute
import com.github.bumblebee202111.doubean.model.Result
import com.github.bumblebee202111.doubean.model.groups.Poll
import com.github.bumblebee202111.doubean.model.groups.PollId
import com.github.bumblebee202111.doubean.model.groups.Question
import com.github.bumblebee202111.doubean.model.groups.QuestionId
import com.github.bumblebee202111.doubean.model.groups.TopicCommentSortBy
import com.github.bumblebee202111.doubean.model.groups.TopicContentEntityId
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val pollRepository: PollRepository,
    topicRepo: GroupTopicRepo,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val topicId = savedStateHandle.toRoute<TopicRoute>().topicId

    private val commentsData = topicRepo.getTopicCommentsData(topicId)

    val popularComments =
        commentsData.first

    val allComments = commentsData.second.cachedIn(viewModelScope)

    private val _commentsSortBy: MutableStateFlow<TopicCommentSortBy> =
        MutableStateFlow(TopicCommentSortBy.ALL)
    val commentsSortBy = _commentsSortBy.asStateFlow()

    val shouldShowSpinner = commentsData.first.map { it.isNotEmpty() }.stateInUi(false)

    private val topicResult = topicRepo.getTopic(topicId).flowOn(Dispatchers.IO).stateInUi()

    val topic = topicResult.map { it?.data }.stateInUi()

    val contentHtml = topicResult.map {
        if (it == null || it is Result.Loading) return@map null
        val content = it.data?.content ?: return@map null

        val regex =
            "(<div data-entity-type=\"(poll|question)\" data-id=\"(\\d*)\">)<div class=\"(poll|question)-wrapper\"><div class=\"(poll|question)-title\"><span>[^<>]*</span></div></div>(</div>)".toRegex()
        val matchResults = regex.findAll(content)

        val ids: List<TopicContentEntityId> = matchResults.map { matchResult ->
            val groupValues = matchResult.groupValues
            when (groupValues[2]) {
                "poll" -> {
                    PollId(groupValues[3])
                }

                "question" -> {
                    QuestionId(groupValues[3])
                }

                else -> {
                    throw IllegalArgumentException()
                }
            }
        }.toList()
        val entities = pollRepository.getPollsAndQuestions(ids).getOrNull() ?: return@map null
        var index = 0
        val expandedContentElement = content.replace(regex) { matchResult ->
            val groupValues = matchResult.groupValues
            buildString {
                append(groupValues[1])
                when (val entity = entities[index++]) {
                    is Poll -> {
                        val showOptionCounts =
                            entity.options.find { option -> option.votedUserCount > 0 } != null

                        val optionElements = entity.options.joinToString("") { option ->
                            """
<label class="poll-label">
    <span class="poll-checkbox">
        <input type="radio">
    </span>
    <span class="poll-option">
        <span class="poll-option-title">${option.title.htmlEncode()}</span>
        <span class="poll-option-voted-count">${
                                if (showOptionCounts) {
                                    "${option.votedUserCount}（${roundedPercentage(option.votedUserCount.toFloat() / entity.votedUserCount)}%"
                                } else ""
                            }</span>
    </span>
</label>
""".trimIndent()
                        }
                        append(
                            """
<div class="poll-wrapper rendered">
    <div class="poll-title">
        <span>${entity.title.htmlEncode()}(${
                                if (entity.votedLimit == 1) "单选" else "最多选${entity.votedLimit}项"
                            })</span>
        ${
                                entity.expireTime?.let { expireTime ->
                                    "<span class=\"poll-expire-time\"> · Poll expires at $expireTime</span>"
                                } ?: ""

                            }
    </div>
    <div class="poll-meta">
        <span>${entity.votedUserCount}人参与</span>
    </div>
    <div class="poll-content">
        <form class="poll-options">
           $optionElements
        </form>
    </div>
</div>
                        """.trimIndent()
                        )
                    }

                    is Question -> {
                        append(
                            """
<div class="question-wrapper rendered${if (entity.correctAnswer.isNotBlank()) " has-correct" else ""}">
    <div class="question-title">
        <span>${entity.title.htmlEncode()}</span>
        ${
                                entity.expireTime?.let { expireTime ->
                                    "<span class=\"question-expire-time\"> · Question expires at $expireTime</span>"
                                } ?: ""
                            }
    </div>
    <div class="question-meta"><span>${entity.postedUserCount}人参与</span></div>
    ${
                                entity.correctAnswer.takeUnless { answer -> answer.isBlank() }
                                    ?.let { correctAnswer ->
                                        """
        <div class="question-content">
        <form>
            <div class="question-result">
                <div class="question-result-answer">正确答案：$correctAnswer</div>
            </div>
        </form>
    </div>
    """.trimIndent()
                                    } ?: ""
                            }
</div>
                        """.trimIndent()
                        )
                    }
                }
                append(groupValues[6])
            }

        }
        Log.d("Topic detail", "content: $expandedContentElement")
        return@map """
<!DOCTYPE html>
<head>
    <meta name="viewport" content="width=device-width, height=device-height, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, viewport-fit=cover" />
</head>
<body>
    $expandedContentElement
<body>
                    """.trimIndent()
    }.stateInUi()

    val shouldShowPhotoList =
        topicResult.map { it?.data?.content?.contains("image-container") == false }.stateInUi()

    var shouldDisplayInvalidImageUrl by mutableStateOf(false)

    fun updateCommentsSortBy(commentSortBy: TopicCommentSortBy) {
        _commentsSortBy.value = commentSortBy
    }

    private fun roundedPercentage(f: Float): Double {
        return f.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

    fun displayInvalidImageUrl() {
        shouldDisplayInvalidImageUrl = true
    }

    fun clearInvalidImageUrlState() {
        shouldDisplayInvalidImageUrl = false
    }

}