package com.github.bumblebee202111.doubean.feature.groups.postDetail

import android.util.Log
import androidx.core.text.htmlEncode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.GroupRepository
import com.github.bumblebee202111.doubean.data.repository.PollRepository
import com.github.bumblebee202111.doubean.model.Poll
import com.github.bumblebee202111.doubean.model.PollId
import com.github.bumblebee202111.doubean.model.Question
import com.github.bumblebee202111.doubean.model.QuestionId
import com.github.bumblebee202111.doubean.model.Resource
import com.github.bumblebee202111.doubean.model.Status
import com.github.bumblebee202111.doubean.model.TopicContentEntityId
import com.github.bumblebee202111.doubean.ui.common.NextPageHandler
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val pollRepository: PollRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val postId = PostDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).postId

    private val reloadTrigger = MutableLiveData(Unit)
    private val nextPageHandler = object : NextPageHandler() {
        override fun loadNextPageFromRepo(): LiveData<Resource<Boolean>?> {
            return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(groupRepository.getNextPagePostComments(postId))
            }
        }
    }

    val contentHtml = MutableStateFlow<String?>(null)

    val post = groupRepository.getPost(postId).onEach { it ->

        if (it.status == Status.LOADING) return@onEach
        val content = it.data?.content ?: return@onEach

        val regex =
            "(<div data-entity-type=\"(poll|question)\" data-id=\"(\\d*)\">)<div class=\"(poll|question)-wrapper\"><div class=\"(poll|question)-title\"><span>[^<>]*</span></div></div>(</div>)".toRegex()
        val matchResult = regex.findAll(content)

        val ids: List<TopicContentEntityId> = matchResult.map {
            val groupValues = it.groupValues
            return@map when (groupValues[2]) {
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
        val entities = pollRepository.getPollsAndQuestions(ids).getOrNull() ?: return@onEach
        var index = 0
        val expandedContentElement = content.replace(regex) { it ->
            val groupValues = it.groupValues
            buildString {
                append(groupValues[1])
                when (val entity = entities[index++]) {
                    is Poll -> {
                        val showOptionCounts = entity.options.find { it.votedUserCount > 0 } != null

                        val optionElements = entity.options.joinToString("") {
                            """
<label class="poll-label">
    <span class="poll-checkbox">
        <input type="radio">
    </span>
    <span class="poll-option">
        <span class="poll-option-title">${it.title.htmlEncode()}</span>
        <span class="poll-option-voted-count">${
                                if (showOptionCounts) {
                                    "${it.votedUserCount}（${roundedPercentage(it.votedUserCount.toFloat() / entity.votedUserCount)}%"
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
                                entity.expireTime?.let {
                                    "<span class=\"poll-expire-time\"> · Poll expires at $it</span>"
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
                                entity.expireTime?.let {
                                    "<span class=\"question-expire-time\"> · Question expires at $it</span>"
                                } ?: ""
                            }
    </div>
    <div class="question-meta"><span>${entity.postedUserCount}人参与</span></div>
    ${
                                entity.correctAnswer.takeUnless { answer -> answer.isBlank() }
                                    ?.let {
                                        """
        <div class="question-content">
        <form>
            <div class="question-result">
                <div class="question-result-answer">正确答案：$it</div>
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
        Log.d("Post detail", "content: $expandedContentElement")
        contentHtml.value = """
<!DOCTYPE html>
<head>
    <meta name="viewport" content="width=device-width, height=device-height, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, viewport-fit=cover" />
</head>
<body>
    $expandedContentElement
<body>
                    """.trimIndent()
    }.flowOn(Dispatchers.IO).stateInUi()


    val postComments = reloadTrigger.switchMap {
        groupRepository.getPostComments(postId).flowOn(Dispatchers.IO).asLiveData()

    }

    fun refreshPostComments() {
        reloadTrigger.value = Unit
    }

    val loadMoreStatus
        get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        nextPageHandler.loadNextPage()
    }

    private fun roundedPercentage(f: Float): Double {
        return f.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }
}