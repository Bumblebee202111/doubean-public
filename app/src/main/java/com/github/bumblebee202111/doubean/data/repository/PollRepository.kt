package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.model.groups.PollId
import com.github.bumblebee202111.doubean.model.groups.QuestionId
import com.github.bumblebee202111.doubean.model.groups.TopicContentEntityId
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.asExternalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollRepository @Inject constructor(private val ApiService: ApiService) {
    suspend fun getPollsAndQuestions(pollIds: List<TopicContentEntityId>) =
        withContext(Dispatchers.IO) {
            suspendRunCatching {
                pollIds.map { id ->
                    async {
                        when (id) {
                            is PollId -> ApiService.getPoll(id.s).asExternalModel()
                            is QuestionId -> {
                                ApiService.getQuestion(id.s).asExternalModel()
                            }
                        }

                    }
                }.awaitAll()
            }

        }

}