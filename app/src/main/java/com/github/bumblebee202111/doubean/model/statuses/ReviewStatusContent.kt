package com.github.bumblebee202111.doubean.model.statuses

data class SubjectLabel(
    val icon: String,
    val uri: String,
    val title: String,
)

data class ReviewCard(
    val subjectLabel: SubjectLabel,
    val abstract: String,
    val authorUri: String,
    val authorName: String,
    val title: String,
) : StatusCardData
