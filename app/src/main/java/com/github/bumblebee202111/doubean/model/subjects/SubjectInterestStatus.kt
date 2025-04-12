package com.github.bumblebee202111.doubean.model.subjects

import com.github.bumblebee202111.doubean.network.model.NetworkSubjectInterestStatus

enum class SubjectInterestStatus {
    
    MARK_STATUS_DOING,
    MARK_STATUS_DONE,
    MARK_STATUS_MARK,
    MARK_STATUS_UNMARK
    ;
}

fun SubjectInterestStatus.toNetworkStatus(): NetworkSubjectInterestStatus {
    return when (this) {
        SubjectInterestStatus.MARK_STATUS_DOING -> NetworkSubjectInterestStatus.MARK_STATUS_DOING
        SubjectInterestStatus.MARK_STATUS_DONE -> NetworkSubjectInterestStatus.MARK_STATUS_DONE
        SubjectInterestStatus.MARK_STATUS_MARK -> NetworkSubjectInterestStatus.MARK_STATUS_MARK
        SubjectInterestStatus.MARK_STATUS_UNMARK -> NetworkSubjectInterestStatus.MARK_STATUS_UNMARK
    }
}