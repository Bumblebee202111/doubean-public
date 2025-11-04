package com.github.bumblebee202111.doubean.model.subjects

sealed interface SubjectModule {
    data class SelectedCollections(
        val selectedCollections: List<SubjectCollectionItem>,
    ) : SubjectModule

    data class SubjectUnions(
        val subjectUnions: List<SubjectUnion>,
    ) : SubjectModule {
        data class SubjectUnion(
            val title: String,
            val items: List<SubjectWithInterest<*>>,
        )
    }
}