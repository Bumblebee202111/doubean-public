package com.github.bumblebee202111.doubean.shared.subject.data

import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectInterestStatus
import com.github.bumblebee202111.doubean.shared.subject.model.SubjectInterestStatus


fun SubjectInterestStatus.toNetworkStatus(): NetworkSubjectInterestStatus {
    return when (this) {
        SubjectInterestStatus.MARK_STATUS_DOING -> NetworkSubjectInterestStatus.MARK_STATUS_DOING
        SubjectInterestStatus.MARK_STATUS_DONE -> NetworkSubjectInterestStatus.MARK_STATUS_DONE
        SubjectInterestStatus.MARK_STATUS_MARK -> NetworkSubjectInterestStatus.MARK_STATUS_MARK
        SubjectInterestStatus.MARK_STATUS_UNMARK -> NetworkSubjectInterestStatus.MARK_STATUS_UNMARK
    }
}