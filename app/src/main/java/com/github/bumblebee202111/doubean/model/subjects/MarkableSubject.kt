package com.github.bumblebee202111.doubean.model.subjects

interface MarkableSubject {
    val id: String
    val type: SubjectType
    val interest: SubjectInterest?
}