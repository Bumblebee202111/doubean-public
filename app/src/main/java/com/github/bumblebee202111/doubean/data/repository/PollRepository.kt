package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.model.PollId
import com.github.bumblebee202111.doubean.model.QuestionId
import com.github.bumblebee202111.doubean.model.TopicContentEntityId
import com.github.bumblebee202111.doubean.network.ApiKtorService
import com.github.bumblebee202111.doubean.network.model.asExternalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollRepository @Inject constructor(private val ApiKtorService: ApiKtorService) {
    suspend fun getPollsAndQuestions(pollIds: List<TopicContentEntityId>) =
        withContext(Dispatchers.IO) {
            suspendRunCatching {
                pollIds.map { id ->
                    async {
                        when (id) {
                            is PollId -> ApiKtorService.getPoll(id.s).asExternalModel()
                            is QuestionId -> {
                                ApiKtorService.getQuestion(id.s).asExternalModel()
                            }
                        }

                    }
                }.awaitAll()
            }

        }

}