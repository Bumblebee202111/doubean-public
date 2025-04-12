package com.github.bumblebee202111.doubean.model.subjects

data class MySubjectStatus(
    val status: SubjectInterestStatus,
    val count: Int,
    val titleEng: String,
    val title: String,
    val subjectCoverUrl: String?,
) {
    val titleDisplayEng = titleEng.replaceFirstChar(Char::uppercaseChar)
}
