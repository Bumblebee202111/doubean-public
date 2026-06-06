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
            val collections: List<SubjectWithInterest<*>>,
        )
    }

    
    data class SubjectUnion(
        val title: String,
        val collections: List<SubjectUnionCollection>,
    ) : SubjectModule

    data class SubjectUnionCollection(
        val title: String,
        val items: List<SubjectWithInterest<*>>,
    )

    
    data class CollectionBoard(
        val id: String,
        val title: String,
        val items: List<SubjectWithInterest<*>>,
    ) : SubjectModule
}