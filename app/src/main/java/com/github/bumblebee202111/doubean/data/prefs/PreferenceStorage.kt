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
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_AUTO_IMPORT_SESSION_AT_STARTUP
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_DOUBAN_USER_ID
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_GROUP_NOTIFICATIONS_DEFAULT_ENABLE_NOTIFICATIONS
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_GROUP_NOTIFICATIONS_DEFAULT_SORT_BY
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_GROUP_NOTIFICATIONS_MAX_TOPICS_PER_FETCH
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_GROUP_NOTIFICATIONS_NOTIFY_ON_UPDATES
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_RECEIVE_NOTIFICATIONS
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_START_APP_WITH_GROUPS
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.PreferencesKeys.PREF_UDID
import com.github.bumblebee202111.doubean.model.GroupNotificationPreferences
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
        val PREF_AUTO_IMPORT_SESSION_AT_STARTUP =
            booleanPreferencesKey("pref_auto_import_session_at_startup")

        val PREF_GROUP_NOTIFICATIONS_DEFAULT_ENABLE_NOTIFICATIONS =
            booleanPreferencesKey("group_notifications_default_enable_notifications")
        val PREF_GROUP_NOTIFICATIONS_NOTIFY_ON_UPDATES =
            booleanPreferencesKey("group_notifications_default_notify_on_updates")
        val PREF_GROUP_NOTIFICATIONS_DEFAULT_SORT_BY =
            stringPreferencesKey("group_notifications_default_sort_by")
        val PREF_GROUP_NOTIFICATIONS_MAX_TOPICS_PER_FETCH =
            intPreferencesKey("group_notifications_default_max_topics_per_fetch")
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

    val preferToAutoImportSessionAtStartup = dataStore.data.map {
        it[PREF_AUTO_IMPORT_SESSION_AT_STARTUP] ?: true
    }

    suspend fun preferToAutoImportSessionAtStartup(prefer: Boolean) {
        dataStore.edit {
            it[PREF_AUTO_IMPORT_SESSION_AT_STARTUP] = prefer
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

    val defaultGroupNotificationPreferences = dataStore.data.map { p ->
        GroupNotificationPreferences(
            notificationsEnabled = p[PREF_GROUP_NOTIFICATIONS_DEFAULT_ENABLE_NOTIFICATIONS] ?: true,
            sortBy = p[PREF_GROUP_NOTIFICATIONS_DEFAULT_SORT_BY]?.let { TopicSortBy.valueOf(it) }
                ?: TopicSortBy.HOT_LAST_CREATED,
            maxTopicsPerFetch = p[PREF_GROUP_NOTIFICATIONS_MAX_TOPICS_PER_FETCH] ?: 3,
            notifyOnUpdates = p[PREF_GROUP_NOTIFICATIONS_NOTIFY_ON_UPDATES] ?: false,
        )
    }

    suspend fun setDefaultGroupNotificationPreferences(preferences: GroupNotificationPreferences) {
        dataStore.edit {
            it[PREF_GROUP_NOTIFICATIONS_DEFAULT_ENABLE_NOTIFICATIONS] =
                preferences.notificationsEnabled
            it[PREF_GROUP_NOTIFICATIONS_DEFAULT_SORT_BY] = preferences.sortBy.toString()
            it[PREF_GROUP_NOTIFICATIONS_MAX_TOPICS_PER_FETCH] = preferences.maxTopicsPerFetch
            it[PREF_GROUP_NOTIFICATIONS_NOTIFY_ON_UPDATES] = preferences.notifyOnUpdates
        }
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