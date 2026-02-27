package com.github.bumblebee202111.doubean.navigation

import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import com.example.nav3recipes.deeplink.basic.util.DeepLinkMatcher
import com.example.nav3recipes.deeplink.basic.util.DeepLinkPattern
import com.example.nav3recipes.deeplink.basic.util.DeepLinkRequest
import com.example.nav3recipes.deeplink.basic.util.KeyDecoder
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.navigation.CreatedDouListsNavKey
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.GroupDetailNavKey
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.GroupsHomeNavKey
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.TopicNavKey
import com.github.bumblebee202111.doubean.feature.login.navigation.VerifyPhoneNavKey

private val doubeanDeepLinkPatterns: List<DeepLinkPattern<out NavKey>> = listOf(
    
    DeepLinkPattern(GroupsHomeNavKey.serializer(), "https:
    DeepLinkPattern(GroupsHomeNavKey.serializer(), "https:

    
    DeepLinkPattern(GroupDetailNavKey.serializer(), "https:
    DeepLinkPattern(
        GroupDetailNavKey.serializer(),
        "https:
    ),

    
    DeepLinkPattern(
        TopicNavKey.serializer(),
        "https:
    ),
    DeepLinkPattern(
        TopicNavKey.serializer(),
        "douban:
    ),

    
    DeepLinkPattern(
        CreatedDouListsNavKey.serializer(),
        "douban:
    ),

    
    DeepLinkPattern(
        VerifyPhoneNavKey.serializer(),
        "douban:
    )
)

fun String.toNavKeyOrNull(): NavKey? {
    val uri = try {
        this.toUri()
    } catch (_: Exception) {
        return null
    }

    val request = DeepLinkRequest(uri)
    val match = doubeanDeepLinkPatterns.firstNotNullOfOrNull { pattern ->
        DeepLinkMatcher(request, pattern).match()
    }

    return match?.let {
        KeyDecoder(match.args).decodeSerializableValue(match.serializer)
    }
}