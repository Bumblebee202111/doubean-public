package com.github.bumblebee202111.doubean.model.groups

sealed interface TopicContentEntityId

@JvmInline
value class PollId(val s: String) : TopicContentEntityId

@JvmInline
value class QuestionId(val s: String) : TopicContentEntityId