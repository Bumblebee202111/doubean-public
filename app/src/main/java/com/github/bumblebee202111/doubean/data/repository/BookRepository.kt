package com.github.bumblebee202111.doubean.data.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(private val subjectCollectionRepository: SubjectCollectionRepository) {
    suspend fun getTop250BooksCollection() =
        subjectCollectionRepository.getTop250BooksCollection()

    suspend fun getTop250BooksCollectionItems() =
        subjectCollectionRepository.getTop250BooksCollectionItems()

}