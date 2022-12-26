/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doubean.ford.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.doubean.ford.api.ApiResponse;
import com.doubean.ford.api.DoubanService;
import com.doubean.ford.api.ListResponse;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.vo.Item;
import com.doubean.ford.data.vo.ListResult;
import com.doubean.ford.data.vo.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A task that reads the result in the database and fetches the next page, if it has one.
 */
public abstract class FetchNextPageTask<ItemType extends Item, ResultType extends ListResult, RequestType extends ListResponse<ItemType>> implements Runnable {
    private final MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
    //private final String query;
    private final DoubanService doubanService;
    private final AppDatabase db;

    FetchNextPageTask(DoubanService doubanService, AppDatabase db) {
        this.doubanService = doubanService;
        this.db = db;
    }

    @Override
    public void run() {
        ResultType current = loadFromDb();
        if (current == null) {
            liveData.postValue(null);
            return;
        }
        final Integer nextPageStart = current.next;
        if (nextPageStart == null) {
            liveData.postValue(Resource.success(false));
            return;
        }
        try {
            Response<RequestType> response = createCall(nextPageStart).execute();
            ApiResponse<RequestType> apiResponse = new ApiResponse<>(response);
            if (apiResponse.isSuccessful()) {
                // we merge all repo ids into 1 list so that it is easier to fetch the result list.
                List<String> ids = new ArrayList<>();
                ids.addAll(current.ids);
                //noinspection ConstantConditions
                ids.addAll(apiResponse.body.getIds());
                ResultType merged = merge(ids, apiResponse.body.getTotal(), apiResponse.body.getNextPageStart());
                db.runInTransaction(() -> {
                    saveMergedResult(merged, apiResponse.body.getItems());
                });
                liveData.postValue(Resource.success(apiResponse.body.getNextPageStart() != null));
            } else {
                liveData.postValue(Resource.error(apiResponse.errorMessage, true));
            }
        } catch (IOException e) {
            liveData.postValue(Resource.error(e.getMessage(), true));
        }
    }

    LiveData<Resource<Boolean>> getLiveData() {
        return liveData;
    }

    protected abstract ResultType loadFromDb();

    protected abstract Call<RequestType> createCall(Integer nextPageStart);

    protected abstract void saveMergedResult(@NonNull ResultType item, List<ItemType> items);

    protected abstract ResultType merge(List<String> ids, int total, Integer nextPageStart);
}
