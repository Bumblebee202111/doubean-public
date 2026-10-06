package com.github.bumblebee202111.doubean.data.subject

import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectCollection
import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectCollectionWithItems
import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectInterestStatus
import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectType
import com.github.bumblebee202111.doubean.core.network.model.fangorns.asEntityAndExternalModel
import com.github.bumblebee202111.doubean.core.network.model.fangorns.toUser
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkCelebrity
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkCreditList
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkRecommendSubject
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkSubjectReview
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkSubjectReviewList
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkVendor
import com.github.bumblebee202111.doubean.core.network.model.subject.toSubjectInterest
import com.github.bumblebee202111.doubean.core.network.model.toBackgroundColorScheme
import com.github.bumblebee202111.doubean.core.network.model.toNonNullRating
import com.github.bumblebee202111.doubean.core.network.model.toRating
import com.github.bumblebee202111.doubean.core.network.model.toSubjectType
import com.github.bumblebee202111.doubean.core.network.model.toSubjectWithRank
import com.github.bumblebee202111.doubean.model.subject.Celebrity
import com.github.bumblebee202111.doubean.model.subject.CreditList
import com.github.bumblebee202111.doubean.model.subject.RecommendSubject
import com.github.bumblebee202111.doubean.model.subject.SubjectCollection
import com.github.bumblebee202111.doubean.model.subject.SubjectCollectionItem
import com.github.bumblebee202111.doubean.model.subject.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subject.SubjectReview
import com.github.bumblebee202111.doubean.model.subject.SubjectReviewList
import com.github.bumblebee202111.doubean.model.subject.SubjectType
import com.github.bumblebee202111.doubean.model.subject.Vendor


fun SubjectInterestStatus.toNetworkStatus(): NetworkSubjectInterestStatus {
    return when (this) {
        SubjectInterestStatus.MARK_STATUS_DOING -> NetworkSubjectInterestStatus.MARK_STATUS_DOING
        SubjectInterestStatus.MARK_STATUS_DONE -> NetworkSubjectInterestStatus.MARK_STATUS_DONE
        SubjectInterestStatus.MARK_STATUS_MARK -> NetworkSubjectInterestStatus.MARK_STATUS_MARK
        SubjectInterestStatus.MARK_STATUS_UNMARK -> NetworkSubjectInterestStatus.MARK_STATUS_UNMARK
    }
}

fun NetworkCreditList.toCreditList() = CreditList(
    items = items?.map(NetworkCelebrity::toCelebrity) ?: emptyList(),
    total = total ?: 0
)

fun NetworkCelebrity.toCelebrity() = Celebrity(
    id = id,
    name = name,
    character = simpleCharacter,
    avatarUrl = avatar.normal
)

fun NetworkVendor.toVendor() = Vendor(
    id = id,
    title = title,
    url = url,
    uri = uri,
    icon = icon,
    grayIcon = grayIcon,
    paymentDesc = paymentDesc
)


fun NetworkSubjectCollectionWithItems.toSubjectCollectionItem() = SubjectCollectionItem(
    id = id,
    type = type,
    name = name,
    items = items.mapIndexed { index, item -> item.toSubjectWithRank(index + 1) },
    headerBgImage = headerBgImage,
    colorScheme = colorScheme.toBackgroundColorScheme()
)

fun NetworkSubjectCollection.toSubjectCollection() = SubjectCollection(
    id = id, title = title, total = total
)

fun NetworkSubjectReview.toSubjectReview() = SubjectReview(
    rating = rating?.toNonNullRating(),
    usefulCount = usefulCount,
    sharingUrl = sharingUrl,
    title = title,
    url = url,
    abstract = abstract,
    uri = uri,
    photos = photos.map { it.asEntityAndExternalModel() },
    reactionsCount = reactionsCount,
    commentsCount = commentsCount, user = user.toUser(),
    createTime = createTime,
    resharesCount = resharesCount,
    id = id,
    subjectType = NetworkSubjectType.of(subject.type).toSubjectType()
)


fun NetworkSubjectReviewList.toSubjectReviewList() = SubjectReviewList(
    count = count,
    start = start,
    total = total,
    reviews = reviews.map(NetworkSubjectReview::toSubjectReview)
)

fun NetworkRecommendSubject.toRecommendSubject(): RecommendSubject {
    return RecommendSubject(
        id = id,
        title = title,
        imageUrl = picture.normal,
        rating = rating.toRating(),
        type = SubjectType.fromString(type),
        uri = uri,
        cardSubtitle = cardSubtitle,
        interest = interest?.toSubjectInterest()
    )
}