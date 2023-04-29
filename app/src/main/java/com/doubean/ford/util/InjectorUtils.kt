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
import com.doubean.ford.api.DoubanService
import com.doubean.ford.data.db.AppDatabase.Companion.getInstance
import com.doubean.ford.data.repository.GroupRepository
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository
import com.doubean.ford.ui.groups.groupDetail.GroupDetailViewModelFactory
import com.doubean.ford.ui.groups.groupSearch.GroupSearchViewModelFactory
import com.doubean.ford.ui.groups.groupTab.GroupTabViewModelFactory
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeViewModelFactory
import com.doubean.ford.ui.groups.postDetail.PostDetailViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {
    private fun provideDoubanService(): DoubanService {
        return DoubanService.create()
    }

    private fun getGroupRepository(context: Context): GroupRepository? {
        return GroupRepository.getInstance(
            AppExecutors(),
            getInstance(context.applicationContext)!!,
            provideDoubanService()
        )
    }

    private fun getGroupFollowsAndSavesRepository(context: Context): GroupsFollowsAndSavesRepository? {
        return GroupsFollowsAndSavesRepository.getInstance(
            AppExecutors(),
            getInstance(context.applicationContext)!!,
            provideDoubanService()
        )
    }

    fun provideGroupsViewModelFactory(context: Context): GroupsHomeViewModelFactory {
        return GroupsHomeViewModelFactory(
            getGroupRepository(context)!!,
            getGroupFollowsAndSavesRepository(context)!!
        )
    }

    fun provideGroupDetailViewModelFactory(
        context: Context,
        groupId: String,
        defaultTab: String?
    ): GroupDetailViewModelFactory {
        return GroupDetailViewModelFactory(
            getGroupRepository(context)!!,
            getGroupFollowsAndSavesRepository(context)!!,
            groupId,
            defaultTab
        )
    }

    fun providePostDetailViewModelFactory(
        context: Context,
        postId: String
    ): PostDetailViewModelFactory {
        return PostDetailViewModelFactory(getGroupRepository(context)!!, postId)
    }

    fun provideGroupTabViewModelFactory(
        context: Context,
        groupId: String,
        tagId: String?
    ): GroupTabViewModelFactory {
        return GroupTabViewModelFactory(
            getGroupRepository(context)!!,
            getGroupFollowsAndSavesRepository(context)!!,
            groupId,
            tagId
        )
    }

    fun provideGroupSearchViewModelFactory(context: Context): GroupSearchViewModelFactory {
        return GroupSearchViewModelFactory(getGroupRepository(context)!!)
    }
}