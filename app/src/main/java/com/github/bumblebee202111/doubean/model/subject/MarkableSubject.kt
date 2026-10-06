package com.github.bumblebee202111.doubean.model.subject

interface MarkableSubject {
    val id: String
    val type: SubjectType
    val interest: SubjectInterest?
}