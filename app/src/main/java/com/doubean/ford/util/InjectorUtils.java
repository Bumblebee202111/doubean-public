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

import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.ui.groupDetail.GroupDetailViewModelFactory;
import com.doubean.ford.ui.groups.GroupsViewModelFactory;


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
public class InjectorUtils {

    private static GroupRepository getGroupRepository(Context context) {
        return GroupRepository.getInstance(new AppExecutors(), AppDatabase.getInstance(context.getApplicationContext()), DoubanService.create());
    }

    public static GroupsViewModelFactory provideGroupsViewModelFactory(Context context) {
        return new GroupsViewModelFactory(getGroupRepository(context));
    }

    public static GroupDetailViewModelFactory provideGroupDetailViewModelFactory(Context context, @NonNull String groupId) {
        return new GroupDetailViewModelFactory(getGroupRepository(context), groupId);
    }

}
