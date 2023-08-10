/*
 * Copyright 2019 Shawn Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doubean.ford.util

import android.content.Context
import com.doubean.ford.MainActivityViewModel
import com.doubean.ford.api.DoubanService
import com.doubean.ford.data.db.AppDatabase.Companion.getInstance
import com.doubean.ford.data.prefs.DataStorePreferenceStorage
import com.doubean.ford.data.prefs.DataStorePreferenceStorage.Companion.dataStore
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupUserDataRepository
import com.doubean.ford.notifications.Notifier
import com.doubean.ford.ui.groups.groupDetail.GroupDetailViewModel
import com.doubean.ford.ui.groups.groupSearch.GroupSearchViewModel
import com.doubean.ford.ui.groups.groupTab.GroupTabViewModel
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeViewModel
import com.doubean.ford.ui.groups.postDetail.PostDetailViewModel
import com.doubean.ford.ui.notifications.NotificationsViewModel
import com.doubean.ford.ui.settings.PerFollowDefaultNotificationsPreferencesSettingsViewModel
import com.doubean.ford.ui.settings.SettingsViewModel

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {
    private fun provideDoubanService(): DoubanService {
        return DoubanService.create()
    }

    private fun getGroupRepository(context: Context): GroupRepository? {
        return GroupRepository.getInstance(
            getInstance(context.applicationContext)!!,
            provideDoubanService()
        )
    }

    private fun getPreferenceStorage(context: Context): DataStorePreferenceStorage =
        DataStorePreferenceStorage(context.dataStore)

    private fun getGroupUserDataRepository(context: Context): GroupUserDataRepository? {
        return GroupUserDataRepository.getInstance(
            getInstance(context.applicationContext)!!, provideDoubanService(), Notifier(context)
        )
    }

    fun provideMainActivityViewModelFactory(context: Context) =
        MainActivityViewModel.Companion.Factory(
            getPreferenceStorage(context)
        )

    fun provideGroupsViewModelFactory(context: Context) = GroupsHomeViewModel.Companion.Factory(
        getGroupRepository(context)!!,
        getGroupUserDataRepository(context)!!
    )

    fun provideGroupDetailViewModelFactory(
        context: Context,
        groupId: String,
        defaultTab: String?,
    ) = GroupDetailViewModel.Companion.Factory(
        getGroupRepository(context)!!,
        getGroupUserDataRepository(context)!!,
        getPreferenceStorage(context),
        groupId,
        defaultTab
    )

    fun providePostDetailViewModelFactory(
        context: Context,
        postId: String,
    ) = PostDetailViewModel.Companion.Factory(getGroupRepository(context)!!, postId)

    fun provideGroupTabViewModelFactory(
        context: Context,
        groupId: String,
        tagId: String?,
    ) = GroupTabViewModel.Companion.Factory(
        getGroupRepository(context)!!,
        getGroupUserDataRepository(context)!!,
        getPreferenceStorage(context),
        groupId,
        tagId
    )

    fun provideGroupSearchViewModelFactory(context: Context) =
        GroupSearchViewModel.Companion.Factory(getGroupRepository(context)!!)

    fun provideNotificationsViewModelFactory(context: Context) =
        NotificationsViewModel.Companion.Factory(
            getGroupUserDataRepository(context)!!
        )

    fun provideSettingsViewModelFactory(context: Context) = SettingsViewModel.Companion.Factory(
        getPreferenceStorage(context)
    )

    fun providePerFollowDefaultNotificationsSettingsViewModelFactory(context: Context) =
        PerFollowDefaultNotificationsPreferencesSettingsViewModel.Companion.Factory(
            getPreferenceStorage(context)
        )

}