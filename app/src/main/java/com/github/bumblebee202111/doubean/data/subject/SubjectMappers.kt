package com.github.bumblebee202111.doubean.data.subject

import androidx.core.text.HtmlCompat
import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectCollection
import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectCollectionWithItems
import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectInterestStatus
import com.github.bumblebee202111.doubean.core.network.model.NetworkSubjectType
import com.github.bumblebee202111.doubean.core.network.model.fangorns.NetworkSizedPhoto
import com.github.bumblebee202111.doubean.core.network.model.fangorns.asEntityAndExternalModel
import com.github.bumblebee202111.doubean.core.network.model.fangorns.toPhoto
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkCelebrity
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkCreditList
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkRecommendSubject
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkReviewDetail
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkSubjectReview
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkSubjectReviewList
import com.github.bumblebee202111.doubean.core.network.model.subject.NetworkVendor
import com.github.bumblebee202111.doubean.core.network.model.subject.elessar.NetworkCollection
import com.github.bumblebee202111.doubean.core.network.model.subject.elessar.NetworkElessarAward
import com.github.bumblebee202111.doubean.core.network.model.subject.elessar.NetworkElessarBaseSubject
import com.github.bumblebee202111.doubean.core.network.model.subject.elessar.NetworkElessarModule
import com.github.bumblebee202111.doubean.core.network.model.subject.elessar.NetworkElessarSubject
import com.github.bumblebee202111.doubean.core.network.model.subject.elessar.NetworkExtra
import com.github.bumblebee202111.doubean.core.network.model.subject.elessar.NetworkWork
import com.github.bumblebee202111.doubean.core.network.model.subject.toSubjectInterest
import com.github.bumblebee202111.doubean.core.network.model.toBackgroundColorScheme
import com.github.bumblebee202111.doubean.core.network.model.toNonNullRating
import com.github.bumblebee202111.doubean.core.network.model.toRating
import com.github.bumblebee202111.doubean.core.network.model.toSubjectType
import com.github.bumblebee202111.doubean.core.network.model.toSubjectWithRank
import com.github.bumblebee202111.doubean.data.user.toUser
import com.github.bumblebee202111.doubean.model.subject.Celebrity
import com.github.bumblebee202111.doubean.model.subject.CreditList
import com.github.bumblebee202111.doubean.model.subject.ElessarAward
import com.github.bumblebee202111.doubean.model.subject.ElessarBaseSubject
import com.github.bumblebee202111.doubean.model.subject.ElessarExtra
import com.github.bumblebee202111.doubean.model.subject.ElessarModule
import com.github.bumblebee202111.doubean.model.subject.ElessarSubject
import com.github.bumblebee202111.doubean.model.subject.ElessarWork
import com.github.bumblebee202111.doubean.model.subject.ElessarWorkCollection
import com.github.bumblebee202111.doubean.model.subject.RecommendSubject
import com.github.bumblebee202111.doubean.model.subject.ReviewDetail
import com.github.bumblebee202111.doubean.model.subject.ReviewSubject
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
    avatarUrl = avatar.normal,
    uri = uri,
    url = url,
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

fun NetworkReviewDetail.toReviewDetail() = ReviewDetail(
    id = id,
    title = title,
    abstract = abstract,
    content = content,
    rtype = rtype,
    typeName = typeName,
    uri = uri,
    url = url,
    sharingUrl = sharingUrl,
    createTime = createTime,
    editTime = editTime,
    ipLocation = ipLocation,
    updatedIpLocation = updatedIpLocation,
    activity = activity,
    rating = rating?.toNonNullRating(),
    user = user.toUser(),
    photos = photos.map(NetworkSizedPhoto::asEntityAndExternalModel),
    subject = subject?.toReviewSubject(),
    usefulCount = usefulCount,
    uselessCount = uselessCount,
    reactionsCount = reactionsCount,
    reactionType = reactionType,
    commentsCount = commentsCount,
    resharesCount = resharesCount,
    collectionsCount = collectionsCount,
    readCount = readCount,
    isCollected = isCollected,
    spoiler = spoiler,
    isOriginal = isOriginal,
    isBookJuryReview = isBookJuryReview,
    totalCntForAuthor = totalCntForAuthor,
)

fun NetworkReviewDetail.Subject.toReviewSubject() = ReviewSubject(
    id = id,
    title = title,
    type = SubjectType.fromString(type),
    rawType = type,
    uri = uri,
    url = url,
    coverUrl = coverUrl,
    cardSubtitle = cardSubtitle,
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


private val ElessarStyleTagRegex = Regex("<style>[\\s\\S]*?</style>")


private fun String.cleanElessarDesc(): String {
    if (isBlank()) return ""
    val withoutStyle = replace(ElessarStyleTagRegex, "")
    return HtmlCompat.fromHtml(withoutStyle, HtmlCompat.FROM_HTML_MODE_COMPACT)
        .toString()
        .trim()
}

fun NetworkElessarSubject.toElessarSubject(): ElessarSubject = ElessarSubject(
    id = id,
    title = title,
    latinTitle = latinTitle,
    type = type,
    subType = subType,
    uri = uri,
    url = alt,
    sharingUrl = sharingUrl,
    cover = cover?.asEntityAndExternalModel(),
    coverUrl = resolveCoverUrl(),
    extra = extra?.toElessarExtra(),
    followedCount = followedCount,
    isFollowed = isFollowed,
    desc = desc.cleanElessarDesc(),
    modules = modules.mapNotNull(NetworkElessarModule::toElessarModule),
)

fun NetworkElessarBaseSubject.toElessarBaseSubject(): ElessarBaseSubject = ElessarBaseSubject(
    id = id,
    title = title,
    type = type,
    subType = subType,
    subjectType = SubjectType.fromString(subType.ifEmpty { type }),
    uri = uri,
    url = alt,
    sharingUrl = sharingUrl,
    cover = cover?.asEntityAndExternalModel(),
    coverUrl = resolveCoverUrl(),
    extra = extra?.toElessarExtra(),
)

fun NetworkExtra.toElessarExtra(): ElessarExtra = ElessarExtra(
    headerImage = headerImage,
    info = info?.mapNotNull { pair ->
        if (pair.size >= 2) pair[0] to pair[1] else null
    } ?: emptyList(),
    rating = (ratingGroup?.rating to (ratingGroup?.nullRatingReason ?: "")).toRating(),
    shortInfo = shortInfo,
    year = year,
)

fun NetworkElessarModule.toElessarModule(): ElessarModule? = when (this) {
    is NetworkElessarModule.WorkCollections -> {
        val payload = payload ?: return null
        val collections = payload.collections
            .map(NetworkCollection::toElessarWorkCollection)
            .filter { it.works.isNotEmpty() }
        if (collections.isEmpty()) null
        else ElessarModule.WorkCollections(
            id = payload.id,
            title = payload.title,
            collections = collections,
        )
    }

    is NetworkElessarModule.Photos -> {
        val payload = payload ?: return null
        if (payload.photos.isEmpty()) null
        else ElessarModule.Photos(
            id = payload.id,
            title = payload.title,
            total = payload.total,
            photos = payload.photos.map { it.toPhoto() },
        )
    }

    is NetworkElessarModule.SubjectCollection -> {
        val payload = payload ?: return null
        if (payload.subjects.isEmpty()) null
        else ElessarModule.SubjectCollection(
            id = payload.id,
            title = payload.title,
            total = payload.total,
            subjects = payload.subjects.map(NetworkElessarBaseSubject::toElessarBaseSubject),
        )
    }

    is NetworkElessarModule.AwardResultCollection -> {
        val payload = payload ?: return null
        if (payload.awards.isEmpty()) null
        else ElessarModule.AwardResultCollection(
            id = payload.id,
            title = payload.title,
            total = payload.total,
            uri = payload.uri,
            awards = payload.awards.map(NetworkElessarAward::toElessarAward),
        )
    }

    is NetworkElessarModule.OtherModule -> null
}

fun NetworkCollection.toElessarWorkCollection(): ElessarWorkCollection = ElessarWorkCollection(
    id = id,
    title = title,
    total = total,
    uri = uri,
    works = works.map(NetworkWork::toElessarWork),
)

fun NetworkWork.toElessarWork(): ElessarWork = ElessarWork(
    roles = roles,
    subject = subject.toElessarBaseSubject(),
)

fun NetworkElessarAward.toElessarAward(): ElessarAward = ElessarAward(
    categoryTitle = category.title,
    ceremonyTitle = ceremony.title,
    ceremonyYear = ceremony.year,
    ceremonyPicUrl = ceremony.pic?.normal,
    isWon = isWon,
    subject = subject?.toElessarBaseSubject(),
)