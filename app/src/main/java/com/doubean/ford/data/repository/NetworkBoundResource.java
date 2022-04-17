package com.doubean.ford.data.repository;


import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.doubean.ford.util.AppExecutors;

import java.util.Objects;

/**
 * Simplified version of https://github.com/android/architecture-components-samples/blob/main/GithubBrowserSample/app/src/main/java/com/android/example/github/repository/NetworkBoundResource.kt
 * -No resource/response encapsulation
 * -Will only be updated once, no unnecessary loads
 * Original comment:
 * <p>
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 *
 * @param <ResultType>
 * @param <RequestType>
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final AppExecutors appExecutors;

    private final MediatorLiveData<ResultType> result = new MediatorLiveData<>();

    /**
     * Note that the returned LiveData should always be exactly set once (by BumbleBee202111)
     */
    @MainThread
    NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.setValue(null);
        LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(data);
            } else {
                result.addSource(dbSource, this::setValue);
            }
        });
    }

    @MainThread
    private void setValue(ResultType newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    private void fetchFromNetwork(final ResultType cachedData) {
        LiveData<RequestType> apiResponse = createCall();
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            if (response != null) {
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(response);
                    appExecutors.mainThread().execute(() ->
                            result.addSource(loadFromDb(),
                                    this::setValue)
                    );
                });
            } else {
                onFetchFailed();
                setValue(cachedData);
            }

        });
    }

    protected void onFetchFailed() {

    }

    public LiveData<ResultType> asLiveData() {
        return result;
    }

    @WorkerThread
    protected ResultType processResponse(ResultType response) {
        return response;
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract LiveData<RequestType> createCall();
}
