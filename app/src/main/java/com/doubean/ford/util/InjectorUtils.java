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
import com.doubean.ford.data.repository.GroupFollowingRepository;
import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.ui.groups.groupDetail.GroupDetailViewModelFactory;
import com.doubean.ford.ui.groups.groupSearch.GroupSearchViewModelFactory;
import com.doubean.ford.ui.groups.groupTab.GroupTabViewModelFactory;
import com.doubean.ford.ui.groups.groupsHome.GroupsHomeViewModelFactory;
import com.doubean.ford.ui.groups.postDetail.PostDetailViewModelFactory;


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
public class InjectorUtils {
    private static DoubanService provideDoubanService() {
        return DoubanService.create();
    }

    private static GroupRepository getGroupRepository(Context context) {
        return GroupRepository.getInstance(new AppExecutors(), AppDatabase.getInstance(context.getApplicationContext()), provideDoubanService());
    }

    private static GroupFollowingRepository getGroupFollowedRepository(Context context) {
        return GroupFollowingRepository.getInstance(new AppExecutors(), AppDatabase.getInstance(context.getApplicationContext()), provideDoubanService());
    }

    public static GroupsHomeViewModelFactory provideGroupsViewModelFactory(Context context) {
        return new GroupsHomeViewModelFactory(getGroupRepository(context), getGroupFollowedRepository(context));
    }

    public static GroupDetailViewModelFactory provideGroupDetailViewModelFactory(Context context, @NonNull String groupId, String defaultTab) {
        return new GroupDetailViewModelFactory(getGroupRepository(context), getGroupFollowedRepository(context), groupId, defaultTab);
    }

    public static PostDetailViewModelFactory providePostDetailViewModelFactory(Context context, @NonNull String postId) {
        return new PostDetailViewModelFactory(getGroupRepository(context), postId);
    }

    public static GroupTabViewModelFactory provideGroupTabViewModelFactory(Context context, @NonNull String groupId, @Nullable String tagId) {
        return new GroupTabViewModelFactory(getGroupRepository(context), groupId, tagId);
    }

    public static GroupSearchViewModelFactory provideGroupSearchViewModelFactory(Context context) {
        return new GroupSearchViewModelFactory(getGroupRepository(context));
    }
}
