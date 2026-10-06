package com.github.bumblebee202111.doubean.navigation

import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.navigation.CreatedDouListsNavKey
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.GroupDetailNavKey
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.GroupsHomeNavKey
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.TopicNavKey
import com.github.bumblebee202111.doubean.feature.login.navigation.VerifyPhoneNavKey
import kotlinx.serialization.serializer

private val doubeanDeepLinkMatchers: List<DeepLinkMatcher<NavKey, *>> = listOf(
    
    UriDeepLinkMatcher("https:
    UriDeepLinkMatcher(
        "https:
        serializer<GroupsHomeNavKey>()
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
        "douban:
        serializer<TopicNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<CreatedDouListsNavKey>()
    ),

    
    UriDeepLinkMatcher(
        "douban:
        serializer<VerifyPhoneNavKey>()
    )
)

fun String.toNavKeyOrNull(): NavKey? {
    val uri = try {
        this.toUri()
    } catch (_: Exception) {
        return null
    }

    val request = DeepLinkRequest(uri)
    val matchResult = doubeanDeepLinkMatchers.mapNotNull { matcher ->
        matcher.match(request)
    }.maxOrNull()

    return matchResult?.key
}