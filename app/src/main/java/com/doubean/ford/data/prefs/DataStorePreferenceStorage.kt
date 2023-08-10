package com.doubean.ford.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.doubean.ford.data.prefs.DataStorePreferenceStorage.PreferencesKeys.PREF_PER_FOLLOW_ALLOW_DUPLICATE_NOTIFICATIONS
import com.doubean.ford.data.prefs.DataStorePreferenceStorage.PreferencesKeys.PREF_PER_FOLLOW_DEFAULT_ENABLE_POST_NOTIFICATIONS
import com.doubean.ford.data.prefs.DataStorePreferenceStorage.PreferencesKeys.PREF_PER_FOLLOW_FEED_REQUEST_POST_COUNT_LIMIT
import com.doubean.ford.data.prefs.DataStorePreferenceStorage.PreferencesKeys.PREF_PER_FOLLOW_SORT_RECOMMENDED_POSTS_BY
import com.doubean.ford.data.prefs.DataStorePreferenceStorage.PreferencesKeys.PREF_RECEIVE_NOTIFICATIONS
import com.doubean.ford.model.PostSortBy
import kotlinx.coroutines.flow.map

/**
 * Storage for app and user preferences.
 */
class DataStorePreferenceStorage constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        private const val PREFS_NAME = "doubean"
        val Context.dataStore by preferencesDataStore(
            name = PREFS_NAME
        )
    }

    object PreferencesKeys {
        val PREF_RECEIVE_NOTIFICATIONS = booleanPreferencesKey("pref_receive_notifications")
        val PREF_PER_FOLLOW_DEFAULT_ENABLE_POST_NOTIFICATIONS =
            booleanPreferencesKey("per_follow_enable_post_notifications")
        val PREF_PER_FOLLOW_ALLOW_DUPLICATE_NOTIFICATIONS =
            booleanPreferencesKey("per_follow_allow_duplicate_notifications")
        val PREF_PER_FOLLOW_SORT_RECOMMENDED_POSTS_BY =
            stringPreferencesKey("per_follow_sort_recommended_posts_by")
        val PREF_PER_FOLLOW_FEED_REQUEST_POST_COUNT_LIMIT =
            intPreferencesKey("per_follow_feed_request_post_count_limit")
    }

    suspend fun preferToReceiveNotifications(prefer: Boolean) {
        dataStore.edit {
            it[PREF_RECEIVE_NOTIFICATIONS] = prefer
        }
    }

    val preferToReceiveNotifications = dataStore.data.map {
        it[PREF_RECEIVE_NOTIFICATIONS] ?: true
    }

    suspend fun setPerFollowDefaultEnablePostNotifications(enable: Boolean) {
        dataStore.edit {
            it[PREF_PER_FOLLOW_DEFAULT_ENABLE_POST_NOTIFICATIONS] = enable
        }
    }

    val perFollowDefaultEnablePostNotifications = dataStore.data.map {
        it[PREF_PER_FOLLOW_DEFAULT_ENABLE_POST_NOTIFICATIONS] ?: true
    }

    suspend fun setPerFollowDefaultAllowDuplicateNotifications(allow: Boolean) {
        dataStore.edit {
            it[PREF_PER_FOLLOW_ALLOW_DUPLICATE_NOTIFICATIONS] = allow
        }
    }

    val perFollowDefaultAllowDuplicateNotifications = dataStore.data.map {
        it[PREF_PER_FOLLOW_ALLOW_DUPLICATE_NOTIFICATIONS] ?: false
    }

    suspend fun setPerFollowDefaultSortRecommendedPostsBy(sortRecommendedPostsBy: PostSortBy) {
        dataStore.edit {
            it[PREF_PER_FOLLOW_SORT_RECOMMENDED_POSTS_BY] = sortRecommendedPostsBy.toString()
        }
    }

    val perFollowDefaultSortRecommendedPostsBy = dataStore.data.map { p ->
        p[PREF_PER_FOLLOW_SORT_RECOMMENDED_POSTS_BY]?.let { PostSortBy.valueOf(it) }
            ?: PostSortBy.NEW_TOP
    }

    suspend fun setPerFollowDefaultFeedRequestPostCountLimit(feedRequestPostCountLimit: Int) {
        dataStore.edit {
            it[PREF_PER_FOLLOW_FEED_REQUEST_POST_COUNT_LIMIT] = feedRequestPostCountLimit
        }
    }

    val perFollowDefaultFeedRequestPostCountLimit = dataStore.data.map { p ->
        p[PREF_PER_FOLLOW_FEED_REQUEST_POST_COUNT_LIMIT] ?: 3
    }
}