package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.ImageItem
import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.model.subjects.MovieTrailer
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectReview
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectStatusActionTextResIdsMap
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.ui.component.ExpandCollapseText
import com.github.bumblebee202111.doubean.ui.component.ListItemCount
import com.github.bumblebee202111.doubean.ui.component.ListItemImages
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.util.DateTimeStyle
import com.github.bumblebee202111.doubean.util.OpenInUtils
import com.github.bumblebee202111.doubean.util.toRelativeString
import java.time.LocalDateTime

fun LazyListScope.subjectInfoNormalModuleItem(
    titleResId: Int,
    body: @Composable ColumnScope.() -> Unit,
    total: Int? = null,
) {
    item {
        SubjectInfoModuleItemContent(
            titleResId = titleResId,
            body = body,
            modifier = Modifier.padding(vertical = 16.dp),
            total = total
        )
    }

}

fun LazyListScope.subjectInfoIntroModuleItem(intro: String) {
    subjectInfoNormalModuleItem(titleResId = R.string.subject_module_title_intro, body = {
        ExpandCollapseText(
            text = intro,
            maxLines = IntroMaxLines,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }, null)
}

private const val IntroMaxLines = 4

fun LazyListScope.subjectInfoInterestsModuleItem(
    interestList: SubjectInterestWithUserList,
    onUserClick: (userId: String) -> Unit,
) {
    item {
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(size = 16.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            SubjectInfoModuleItemContent(
                titleResId = R.string.subject_module_title_interests,
                body = {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        val interests = interestList.interests
                        interests.forEach { interest ->
                            Column {
                                if (interest != interests.first()) {
                                    Spacer(modifier = Modifier.size(16.dp))
                                }
                                Row {
                                    UserProfileImage(
                                        url = interest.user.avatar,
                                        size = 40.dp,
                                        onClick = { onUserClick(interest.user.id) }
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Column {
                                        Text(
                                            text = interest.user.name,
                                            modifier = Modifier.clickable {
                                                onUserClick(interest.user.id)
                                            },
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(modifier = Modifier.size(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            interest.rating?.let {
                                                SubjectRatingBar(
                                                    rating = it,
                                                    size = SubjectRatingBarSize.Compact
                                                )
                                                Spacer(modifier = Modifier.size(4.dp))
                                            }
                                            DateTimeText(
                                                text = interest.createTime.toRelativeString(style = DateTimeStyle.ABBREVIATED)
                                            )
                                        }
                                    }
                                }

                                interest.comment?.let { comment ->
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Text(text = comment)
                                }

                                Spacer(modifier = Modifier.size(12.dp))

                                ListItemCount(
                                    iconVector = Icons.Outlined.ThumbUp,
                                    count = interest.voteCount
                                )
                            }
                            if (interest != interests.last()) {
                                Spacer(modifier = Modifier.size(16.dp))
                                HorizontalDivider()
                            }
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp),
                total = interestList.total
            )


        }
    }
}

fun LazyListScope.subjectInfoCelebritiesModuleItem(
    directorNames: List<String>,
    actorNames: List<String>,
) {
    subjectInfoNormalModuleItem(
        titleResId = R.string.subject_module_title_celebrities,
        body = {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = directorNames,
                    contentType = { R.string.director }) { directorName ->
                    SubjectInfoCelebrity(name = directorName, typeNameRes = R.string.director)
                }
                items(
                    items = actorNames,
                    contentType = { R.string.actor }) { actorName ->
                    SubjectInfoCelebrity(name = actorName, typeNameRes = R.string.actor)
                }
            }
        })
}

@Composable
private fun SubjectInfoCelebrity(name: String, typeNameRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name)
        Text(
            text = stringResource(id = typeNameRes),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

fun LazyListScope.subjectInfoTrailersModuleItem(
    trailers: List<MovieTrailer>,
    photoList: PhotoList,
    onImageClick: (url: String) -> Unit,
) {
    subjectInfoNormalModuleItem(
        titleResId = R.string.subject_module_title_trailers,
        body = {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                val trailerHeightModifier = Modifier.height(160.dp)
                items(items = trailers, key = { it.id }) {
                    AsyncImage(
                        model = it.coverUrl,
                        contentDescription = null,
                        modifier = trailerHeightModifier.clickable {
                            onImageClick(it.coverUrl)
                        },
                        contentScale = ContentScale.FillHeight
                    )
                }
                items(items = photoList.photos, key = { it.image.normal.url }) {
                    AsyncImage(
                        model = it.image.normal.url,
                        contentDescription = null,
                        modifier = trailerHeightModifier.clickable {
                            onImageClick(it.image.large.url)
                        },
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
        },
        total = photoList.total
    )
}

@Composable
fun SubjectInfoReviewsModuleItemContent(
    subjectType: SubjectType,
    reviews: SubjectReviewList,
    modifier: Modifier = Modifier,
    onUserClick: (userId: String) -> Unit,
) {
    val titleResId = when (subjectType) {
        SubjectType.MOVIE -> R.string.title_review_movie
        SubjectType.TV -> R.string.title_review_tv
        SubjectType.BOOK -> R.string.title_review_book
        SubjectType.UNSUPPORTED -> throw IllegalArgumentException()
    }
    SubjectInfoModuleItemContent(
        titleResId = titleResId,
        body = {
            LazyColumn(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = reviews.reviews, key = { it.id }) { review ->
                    SubjectReviewCard(review = review, onUserClick = onUserClick)
                }
            }
        },
        modifier = modifier,
        total = reviews.total
    )
}

@Composable
private fun SubjectReviewCard(
    review: SubjectReview,
    modifier: Modifier = Modifier,
    onUserClick: (userId: String) -> Unit,
) {
    val context = LocalContext.current
    Card(modifier = modifier, onClick = {
        OpenInUtils.openInDouban(context = context, uri = review.uri)
    }) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically

            ) {
                UserProfileImage(
                    url = review.user.avatar,
                    size = dimensionResource(R.dimen.icon_size_extra_small),
                    onClick = {
                        onUserClick(review.user.id)
                    }
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = review.user.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onUserClick(review.user.id)
                    }
                )
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    VerticalDivider(Modifier.padding(horizontal = 4.dp))
                    val ratingActionResId =
                        SubjectStatusActionTextResIdsMap.getValue(review.subjectType)
                            .getValue(SubjectInterestStatus.MARK_STATUS_DONE)
                    Text(
                        text = stringResource(id = ratingActionResId),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Light
                    )
                }
                review.rating?.let { rating ->
                    Spacer(modifier = Modifier.size(1.dp))
                    SubjectRatingBar(
                        rating = rating,
                        size = SubjectRatingBarSize.Compact
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                DateTimeText(
                    text = review.createTime.toRelativeString(style = DateTimeStyle.ABBREVIATED),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = review.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(text = review.abstract, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.padding(4.dp))
            ListItemImages(images = review.photos.map(SizedPhoto::image))

            Spacer(modifier = Modifier.padding(4.dp))
            //Useful info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOfNotNull(
                    review.reactionsCount.takeIf { it > 0 }?.let {
                        CountType.REACTIONS to it
                    },
                    review.usefulCount.takeIf { it > 0 }?.let {
                        CountType.USEFUL to it
                    },
                    review.resharesCount.takeIf { it > 0 }?.let {
                        CountType.RESHARES to it
                    }
                ).forEach { (type, count) ->
                    ListItemCount(
                        iconVector = type.icon,
                        count = count
                    )
                }
            }

        }
    }
}

enum class CountType(val icon: ImageVector) {
    REACTIONS(Icons.AutoMirrored.Outlined.Reply),
    USEFUL(Icons.Outlined.ThumbUp),
    RESHARES(Icons.Outlined.Repeat);
}

@Composable
private fun SubjectInfoModuleItemContent(
    titleResId: Int,
    body: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    total: Int? = null,
) {

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = titleResId),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            total?.let {
                Text(text = it.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        body()
    }
}

@Preview
@Composable
private fun SubjectReviewCardPreview() {
    SubjectReviewCard(
        review = SubjectReview(
            rating = null,
            usefulCount = 213,
            sharingUrl = "",
            title = "title",
            url = "url",
            abstract = "213",
            uri = "",
            photos = listOf(
                SizedPhoto(
                    id = "0",
                    description = "",
                    image = SizedImage(
                        ImageItem(
                            height = 1,
                            size = null,
                            url = "https://img1.doubanio.com/view/thing_review/l/public/p3152698.webp",
                            width = 1
                        ),
                        ImageItem(
                            height = 1,
                            size = null,
                            url = "https://img1.doubanio.com/view/thing_review/l/public/p3152698.webp",
                            width = 1
                        )
                    ), false, ""
                )
            ),
            reactionsCount = 123,
            commentsCount = 12312,
            user = User("213", "etho", name = "Ethos", avatar = "", uri = "", alt = ""),
            createTime = LocalDateTime.now(),
            resharesCount = 1,
            id = "0",
            subjectType = SubjectType.MOVIE,
        ),
        onUserClick = {}
    )
}
