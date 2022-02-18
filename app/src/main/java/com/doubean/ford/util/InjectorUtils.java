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

package com.doubean.ford.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.repository.GroupFavoritesRepository;
import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.ui.groupDetail.GroupDetailViewModelFactory;
import com.doubean.ford.ui.groupSearch.GroupSearchViewModelFactory;
import com.doubean.ford.ui.groupTab.GroupTabViewModelFactory;
import com.doubean.ford.ui.groupTopic.GroupTopicViewModelFactory;
import com.doubean.ford.ui.groups.GroupsViewModelFactory;

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
public class InjectorUtils {

    private static GroupRepository getGroupRepository(Context context) {
        return GroupRepository.getInstance(new AppExecutors(), AppDatabase.getInstance(context.getApplicationContext()), DoubanService.create());
    }

    private static GroupFavoritesRepository getFavoriteGroupRepository(Context context) {
        return GroupFavoritesRepository.getInstance(new AppExecutors(), AppDatabase.getInstance(context.getApplicationContext()), DoubanService.create());
    }

    public static GroupsViewModelFactory provideGroupsViewModelFactory(Context context) {
        return new GroupsViewModelFactory(getGroupRepository(context), getFavoriteGroupRepository(context));
    }

    public static GroupDetailViewModelFactory provideGroupDetailViewModelFactory(Context context, @NonNull String groupId, String defaultTab) {
        return new GroupDetailViewModelFactory(getGroupRepository(context), getFavoriteGroupRepository(context), groupId, defaultTab);
    }

    public static GroupTopicViewModelFactory provideGroupTopicDetailViewModelFactory(Context context, @NonNull String topicId) {
        return new GroupTopicViewModelFactory(getGroupRepository(context), topicId);
    }

    public static GroupTabViewModelFactory provideGroupTabViewModelFactory(Context context, @NonNull String groupId, @Nullable String tagId) {
        return new GroupTabViewModelFactory(getGroupRepository(context), groupId, tagId);
    }

    public static GroupSearchViewModelFactory provideGroupSearchViewModelFactory(Context context) {
        return new GroupSearchViewModelFactory(getGroupRepository(context));
    }
}
