package com.github.bumblebee202111.doubean.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.navigation.CreatedDouListsNavKey
import com.github.bumblebee202111.doubean.feature.doulists.doulist.navigation.DouListNavKey
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.GroupDetailNavKey
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.GroupsHomeNavKey
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.TopicNavKey
import com.github.bumblebee202111.doubean.feature.login.navigation.VerifyPhoneNavKey
import com.github.bumblebee202111.doubean.feature.subjects.detail.navigation.SubjectDetailNavKey
import com.github.bumblebee202111.doubean.feature.subjects.elessar.navigation.ElessarSubjectNavKey
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation.RankListNavKey
import com.github.bumblebee202111.doubean.feature.subjects.review.navigation.ReviewNavKey
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.UserProfileNavKey
import com.github.bumblebee202111.doubean.model.subject.SubjectType
import kotlinx.serialization.serializer

private val doubeanDeepLinkMatchers: List<DeepLinkMatcher<NavKey, *>> = listOf(
    
    UriDeepLinkMatcher(
        "douban:
        serializer<GroupsHomeNavKey>()
    ),
    UriDeepLinkMatcher("https:
    UriDeepLinkMatcher(
        "https:
        serializer<GroupsHomeNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<GroupDetailNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<GroupDetailNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<GroupDetailNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "https:
        serializer<TopicNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<TopicNavKey>()
    ),
    UriDeepLinkMatcher(
        "douban:
        serializer<TopicNavKey>()
    ),
    UriDeepLinkMatcher(
        "douban:
        serializer<TopicNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<TopicNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<TopicNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<ReviewNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ReviewNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ReviewNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ReviewNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ReviewNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ReviewNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<DouListNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<DouListNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<DouListNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<CreatedDouListsNavKey>()
    ),
    UriDeepLinkMatcher(
        "douban:
        serializer<CreatedDouListsNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<RankListNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<RankListNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<UserProfileNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<UserProfileNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<UserProfileNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<ElessarSubjectNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ElessarSubjectNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ElessarSubjectNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ElessarSubjectNavKey>()
    ),
    UriDeepLinkMatcher(
        "https:
        serializer<ElessarSubjectNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<VerifyPhoneNavKey>()
    )
)

fun String.toNavKeyOrNull(): NavKey? {
    val rawUri = try {
        this.toUri()
    } catch (_: Exception) {
        return null
    }

    
    val uri = unwrapDoubanDispatchUri(rawUri) ?: rawUri

    matchSubjectOrElessarNavKey(uri)?.let { return it }

    val request = DeepLinkRequest(uri)
    val matchResult = doubeanDeepLinkMatchers.mapNotNull { matcher ->
        matcher.match(request)
    }.maxOrNull()

    return matchResult?.key
}

private fun unwrapDoubanDispatchUri(uri: Uri): Uri? {
    if ((uri.host == "www.douban.com" || uri.host == "m.douban.com") &&
        uri.path?.trimEnd('/') == "/doubanapp/dispatch"
    ) {
        val target = uri.getQueryParameter("uri")?.takeIf { it.isNotBlank() } ?: return null
        val targetUri = when {
            target.startsWith("douban:
            target.startsWith("/") -> "douban:
            else -> "douban:
        }
        if (targetUri.pathSegments.firstOrNull() == "subject" &&
            targetUri.getQueryParameter("subtype") == null
        ) {
            val fallback = uri.getQueryParameter("fallback")?.takeIf { it.isNotBlank() }
            if (fallback != null) {
                return fallback.toUri()
            }
        }
        return targetUri
    }
    return null
}


private fun matchSubjectOrElessarNavKey(uri: Uri): NavKey? {
    val segments = uri.pathSegments
    val scheme = uri.scheme
    val host = uri.host

    return when {
        
        scheme == "douban" && host == "douban.com" &&
                segments.size == 2 && (segments[0] == "celebrity" || segments[0] == "author") -> {
            val subjectId = uri.getQueryParameter("subject_id")
                ?.takeIf { it.isNotBlank() && it.all(Char::isDigit) }
                ?: return null
            ElessarSubjectNavKey(subjectId = subjectId)
        }

        
        scheme == "douban" && host == "douban.com" &&
                segments.size == 2 && segments[0] == "subject" -> {
            val id = segments[1]
            val subType = uri.getQueryParameter("subtype") ?: return null
            if (!id.all(Char::isDigit)) return null
            if (subType == "person") {
                ElessarSubjectNavKey(subjectId = id)
            } else {
                val type = SubjectType.fromString(subType)
                if (type != SubjectType.UNSUPPORTED) {
                    SubjectDetailNavKey(id = id, type = type)
                } else null
            }
        }

        
        scheme == "douban" && host == "douban.com" && segments.size == 2 -> {
            val type = SubjectType.fromString(segments[0])
            val id = segments[1]
            if (type != SubjectType.UNSUPPORTED && id.all(Char::isDigit)) {
                SubjectDetailNavKey(id = id, type = type)
            } else null
        }

        
        (scheme == "https" || scheme == "http") && host == "m.douban.com" &&
                segments.size == 3 && segments[1] == "subject" -> {
            val type = SubjectType.fromString(segments[0])
            val id = segments[2]
            if (type != SubjectType.UNSUPPORTED && id.all(Char::isDigit)) {
                SubjectDetailNavKey(id = id, type = type)
            } else null
        }

        
        (scheme == "https" || scheme == "http") &&
                host in setOf("movie.douban.com", "book.douban.com", "music.douban.com") &&
                segments.size == 2 && segments[0] == "subject" -> {
            val typePrefix = host?.substringBefore(".douban.com") ?: return null
            val type = SubjectType.fromString(typePrefix)
            val id = segments[1]
            if (type != SubjectType.UNSUPPORTED && id.all(Char::isDigit)) {
                SubjectDetailNavKey(id = id, type = type)
            } else null
        }

        else -> null
    }
}