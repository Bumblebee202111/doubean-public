package com.github.bumblebee202111.doubean.data.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(private val subjectCollectionRepository: SubjectCollectionRepository) {
    suspend fun getTop250MoviesCollection() =
        subjectCollectionRepository.getTop250MoviesCollection()

    suspend fun getTop250MoviesCollectionItems() =
        subjectCollectionRepository.getTop250MoviesCollectionItems()

}