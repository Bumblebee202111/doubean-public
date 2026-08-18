package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectInterest

interface MarkableSubject {
    val id: String
    val type: SubjectType
    val interest: SubjectInterest?
}