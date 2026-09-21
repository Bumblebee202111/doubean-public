package com.github.bumblebee202111.doubean.shared.subject.model

interface MarkableSubject {
    val id: String
    val type: SubjectType
    val interest: SubjectInterest?
}