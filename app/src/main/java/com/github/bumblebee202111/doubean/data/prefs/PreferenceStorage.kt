package com.github.bumblebee202111.doubean.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_ACCESS_TOKEN
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_DOUBAN_USER_ID
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_PER_FOLLOW_ALLOW_DUPLICATE_NOTIFICATIONS
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_PER_FOLLOW_DEFAULT_ENABLE_POST_NOTIFICATIONS
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_PER_FOLLOW_feed_request_topic_count_limit
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_PER_FOLLOW_sort_recommended_topics_by
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_RECEIVE_NOTIFICATIONS
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_START_APP_WITH_GROUPS
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_UDID
import com.github.bumblebee202111.doubean.model.TopicSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Singleton


@Singleton
class PreferenceStorage(
    private val dataStore: DataStore<Preferences>, private val json: Json,
) {
    companion object {
        private const val PREFS_NAME = "doubean"
        val Context.dataStore by preferencesDataStore(
            name = PREFS_NAME
        )
    }

    object PreferencesKeys {
        val PREF_RECEIVE_NOTIFICATIONS = booleanPreferencesKey("pref_receive_notifications")
        val PREF_START_APP_WITH_GROUPS = booleanPreferencesKey("pref_start_app_with_groups")


        val PREF_PER_FOLLOW_DEFAULT_ENABLE_POST_NOTIFICATIONS =
            booleanPreferencesKey("per_follow_enable_post_notifications")
        val PREF_PER_FOLLOW_ALLOW_DUPLICATE_NOTIFICATIONS =
            booleanPreferencesKey("per_follow_allow_duplicate_notifications")
        val PREF_PER_FOLLOW_sort_recommended_topics_by =
            stringPreferencesKey("per_follow_sort_recommended_topics_by")
        val PREF_PER_FOLLOW_feed_request_topic_count_limit =
            intPreferencesKey("per_follow_feed_request_topic_count_limit")
        val PREF_UDID = stringPreferencesKey("udid")
        val PREF_ACCESS_TOKEN = stringPreferencesKey("auth_token")
        val PREF_DOUBAN_USER_NAME = stringPreferencesKey("douban_user_name")
        val PREF_EXPIRES_AT = longPreferencesKey("expires_at")
        val PREF_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val PREF_DOUBAN_USER_ID = stringPreferencesKey("douban_user_id")
        val PREF_DOUBAN_SESSION = stringPreferencesKey("douban_session")
    }

    suspend fun preferToReceiveNotifications(prefer: Boolean) {
        dataStore.edit {
            it[PREF_RECEIVE_NOTIFICATIONS] = prefer
        }
    }

    val preferToReceiveNotifications = dataStore.data.map {
        it[PREF_RECEIVE_NOTIFICATIONS] ?: false
    }

    suspend fun setStartAppWithGroups(startAppWithGroups: Boolean) {
        dataStore.edit {
            it[PREF_START_APP_WITH_GROUPS] = startAppWithGroups
        }
    }

    val startAppWithGroups = dataStore.data.map {
        it[PREF_START_APP_WITH_GROUPS] ?: false
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

    suspend fun setPerFollowDefaultSortRecommendedPostsBy(sortRecommendedPostsBy: TopicSortBy) {
        dataStore.edit {
            it[PREF_PER_FOLLOW_sort_recommended_topics_by] = sortRecommendedPostsBy.toString()
        }
    }

    val perFollowDefaultSortRecommendedPostsBy = dataStore.data.map { p ->
        p[PREF_PER_FOLLOW_sort_recommended_topics_by]?.let { TopicSortBy.valueOf(it) }
            ?: TopicSortBy.HOT_LAST_CREATED
    }

    suspend fun setPerFollowDefaultFeedRequestPostCountLimit(feedRequestPostCountLimit: Int) {
        dataStore.edit {
            it[PREF_PER_FOLLOW_feed_request_topic_count_limit] = feedRequestPostCountLimit
        }
    }

    val perFollowDefaultFeedRequestPostCountLimit = dataStore.data.map { p ->
        p[PREF_PER_FOLLOW_feed_request_topic_count_limit] ?: 3
    }

    suspend fun setUDID(udid: String) {
        dataStore.edit {
            it[PREF_UDID] = udid
        }
    }

    val udid = dataStore.data.map { p ->
        p[PREF_UDID]
    }

    suspend fun setAccessToken(accessToken: String?) {
        put(PREF_ACCESS_TOKEN, accessToken)
    }

    val accessToken = dataStore.data.map { p ->
        p[PREF_ACCESS_TOKEN]
    }

    suspend fun setLoggedInUserId(accessToken: String?) {
        put(PREF_DOUBAN_USER_ID, accessToken)
    }

    val loggedInUserId = dataStore.data.map { p ->
        p[PREF_DOUBAN_USER_ID]
    }

    private suspend fun <T> put(key: Preferences.Key<T>, value: T?) {
        dataStore.edit {
            if (value != null)
                it[key] = value
            else
                it.remove(key)
        }
    }

    private fun <T> get(key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data.map { p ->
            p[key]
        }
    }
}