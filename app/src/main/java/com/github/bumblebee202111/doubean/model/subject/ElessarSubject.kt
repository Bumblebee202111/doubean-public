package com.github.bumblebee202111.doubean.model.subject

import com.github.bumblebee202111.doubean.model.Photo
import com.github.bumblebee202111.doubean.model.SizedImage


data class ElessarSubject(
    val id: String,
    val title: String,
    val latinTitle: String,
    val type: String,
    val subType: String,
    val uri: String,
    val url: String,
    val sharingUrl: String,
    val cover: SizedImage?,
    val coverUrl: String,
    val extra: ElessarExtra?,
    val followedCount: Int,
    val isFollowed: Boolean,
    val desc: String,
    val modules: List<ElessarModule>,
)


data class ElessarBaseSubject(
    val id: String,
    val title: String,
    val type: String,
    val subType: String,
    val subjectType: SubjectType,
    val uri: String,
    val url: String,
    val sharingUrl: String,
    val cover: SizedImage?,
    val coverUrl: String,
    val extra: ElessarExtra?,
)


data class ElessarExtra(
    val headerImage: String?,
    val info: List<Pair<String, String>>,
    val rating: Rating,
    val shortInfo: String?,
    val year: String?,
)


sealed interface ElessarModule {
    
    data class WorkCollections(
        val id: String,
        val title: String,
        val collections: List<ElessarWorkCollection>,
    ) : ElessarModule

    
    data class Photos(
        val id: String,
        val title: String,
        val total: Int,
        val photos: List<Photo>,
    ) : ElessarModule

    
    data class SubjectCollection(
        val id: String,
        val title: String,
        val total: Int,
        val subjects: List<ElessarBaseSubject>,
    ) : ElessarModule

    
    data class AwardResultCollection(
        val id: String,
        val title: String,
        val total: Int,
        val uri: String?,
        val awards: List<ElessarAward>,
    ) : ElessarModule
}


data class ElessarWorkCollection(
    val id: String,
    val title: String,
    val total: Int,
    val uri: String?,
    val works: List<ElessarWork>,
)


data class ElessarWork(
    val roles: List<String>,
    val subject: ElessarBaseSubject,
)


data class ElessarAward(
    val categoryTitle: String,
    val ceremonyTitle: String,
    val ceremonyYear: Int,
    val ceremonyPicUrl: String?,
    val isWon: Boolean,
    val subject: ElessarBaseSubject?,
)