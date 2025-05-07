package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.groups.PollId
import com.github.bumblebee202111.doubean.model.groups.QuestionId
import com.github.bumblebee202111.doubean.model.groups.TopicContentEntity
import com.github.bumblebee202111.doubean.model.groups.TopicContentEntityId
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.toPoll
import com.github.bumblebee202111.doubean.network.model.toQuestion
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollRepository @Inject constructor(private val ApiService: ApiService) {
    suspend fun getPollsAndQuestions(pollIds: List<TopicContentEntityId>): AppResult<List<TopicContentEntity>> {
        val results = coroutineScope {
            pollIds.map { id ->
                async {
                    when (id) {
                        is PollId -> {
                            makeApiCall(
                                apiCall = { ApiService.getPoll(id.s) },
                                mapSuccess = { it.toPoll() }
                            )
                        }

                        is QuestionId -> {
                            makeApiCall(
                                apiCall = { ApiService.getQuestion(id.s) },
                                mapSuccess = { it.toQuestion() }
                            )
                        }
                    }
                }
            }.awaitAll()
        }
        val firstError = results.filterIsInstance<AppResult.Error>().firstOrNull()
        if (firstError != null) {
            return firstError
        }

        val successfulData = results.map { (it as AppResult.Success<TopicContentEntity>).data }
        return AppResult.Success(successfulData)
    }

}