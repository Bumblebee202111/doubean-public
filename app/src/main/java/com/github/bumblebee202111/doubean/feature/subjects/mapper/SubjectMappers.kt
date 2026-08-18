package com.github.bumblebee202111.doubean.feature.subjects.mapper

import com.github.bumblebee202111.doubean.feature.subjects.model.Celebrity
import com.github.bumblebee202111.doubean.feature.subjects.model.CreditList
import com.github.bumblebee202111.doubean.feature.subjects.model.RecommendSubject
import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectCollection
import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectCollectionItem
import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectReview
import com.github.bumblebee202111.doubean.feature.subjects.model.SubjectReviewList
import com.github.bumblebee202111.doubean.feature.subjects.model.Vendor
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectCollection
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectCollectionWithItems
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectType
import com.github.bumblebee202111.doubean.network.model.fangorns.asEntityAndExternalModel
import com.github.bumblebee202111.doubean.network.model.fangorns.toUser
import com.github.bumblebee202111.doubean.network.model.subject.NetworkCelebrity
import com.github.bumblebee202111.doubean.network.model.subject.NetworkCreditList
import com.github.bumblebee202111.doubean.network.model.subject.NetworkRecommendSubject
import com.github.bumblebee202111.doubean.network.model.subject.NetworkSubjectReview
import com.github.bumblebee202111.doubean.network.model.subject.NetworkSubjectReviewList
import com.github.bumblebee202111.doubean.network.model.subject.NetworkVendor
import com.github.bumblebee202111.doubean.network.model.subject.toSubjectInterest
import com.github.bumblebee202111.doubean.network.model.toBackgroundColorScheme
import com.github.bumblebee202111.doubean.network.model.toNonNullRating
import com.github.bumblebee202111.doubean.network.model.toRating
import com.github.bumblebee202111.doubean.network.model.toSubjectType
import com.github.bumblebee202111.doubean.network.model.toSubjectWithRank

fun NetworkCreditList.toCreditList() = CreditList(
    items = items.map(NetworkCelebrity::toCelebrity),
    total = total
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
    subjectType = NetworkSubjectType.Companion.of(subject.type).toSubjectType()
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