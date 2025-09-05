package com.github.bumblebee202111.doubean.feature.doulists.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.github.bumblebee202111.doubean.R

@Composable
fun getSystemPrivateAnnotatedText(): AnnotatedString {
    return buildAnnotatedString {
        append("该豆列不符合 ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("社区指导原则")
        }
        append(" ，仅自己可见")
    }
}

fun isSpecificCategory(category: String?): Boolean {
    return category == "movie" || category == "music" || category == "book"
}

@Composable
fun getDouListSubtitle(itemCount: Int, category: String?, followersCount: Int): String {
    val itemsPart: String
    if (isSpecificCategory(category)) {
        val unitResId = when (category) {
            "book" -> R.string.title_book_item_type
            "movie" -> R.string.title_tv_movie_item_type
            "music" -> R.string.title_music_item_type
            else -> 0
        }

        itemsPart = if (unitResId != 0) {
            "$itemCount${stringResource(id = unitResId)}"
        } else {
            stringResource(id = R.string.doulist_content_count, itemCount)
        }
    } else {
        itemsPart = stringResource(id = R.string.doulist_content_count, itemCount)
    }

    val followersPart = if (followersCount > 0) {
        " · ${stringResource(id = R.string.dou_list_follow_count, followersCount)}"
    } else {
        ""
    }

    return "$itemsPart$followersPart"
}

@Composable
fun getDouListLabel(category: String?): String {

    return when (category?.lowercase()) {
        "movie", "tv" -> stringResource(R.string.doulist_movie_label)
        "book" -> stringResource(R.string.doulist_book_label)
        "podcast_episode" -> stringResource(R.string.doulist_podcast_label)
        else -> stringResource(R.string.title_dou_list)
    }
}